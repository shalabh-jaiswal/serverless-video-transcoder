package us.shalabh.svt.lambda;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.shalabh.svt.lambda.model.PostPolicy;
import us.shalabh.svt.lambda.model.S3UploadPolicy;
import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;
import us.shalabh.svt.utils.http.HttpUtils;
import us.shalabh.svt.utils.security.SecurityUtils;

/**
 * Gets the encoded and signed upload post policy for UI to post to S3
 *
 * @author Shalabh Jaiswal
 */
public class GetUploadPolicy implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(GetUploadPolicy.class.getName());

	// bucket name env key
	private static final String ENV_KEY_UPLOAD_VIDEOS_BUCKET = "UPLOAD_VIDEOS_BUCKET";
	private static final String ENV_KEY_S3_BASE_URL = "S3_BASE_URL"; 

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.
	 * lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public ServerlessOutput handleRequest(ServerlessInput input, Context context)
	{
		logger.info("Input: " + input);
		ServerlessOutput output = new ServerlessOutput();

		try
		{
			// create a unique key for file name
			// key = uuid + _ + filename
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			String key = uuid + "_" + input.getQueryStringParameters().get("filename");
			
			// get policy document
			String postPolicy = getPolicyDocument(key);

			// encode to Base64
			String encodedPostPolicy = getEncodedPolicyDocument(postPolicy);

			// sign the policy
			String signature = getPolicySignature(encodedPostPolicy);

			// get response json
			String response = createResponse(key, encodedPostPolicy, signature);
			
			// set output
			HttpUtils.setCORSHeaders(output);
			output.setBody(response);
		}
		catch (Exception e)
		{
			// set error response
			HttpUtils.setInternalServerErrorResponse(output);
		}

		// output
		return output;
	}

	/**
	 * Creates an S3 Upload Post Policy as a JSON
	 * 
	 * @param fileName
	 * @return
	 * @throws JsonProcessingException
	 */
	private String getPolicyDocument(String key) throws JsonProcessingException
	{
		// bucket
		String bucket = System.getenv().get(ENV_KEY_UPLOAD_VIDEOS_BUCKET);
		// expires tomorrow
		String expiration = LocalDateTime.now().plusDays(1).toString() + "Z";

		// conditions
		List<Object> conditions = new ArrayList<>();

		Map<String, String> keyCondition = new HashMap<>();
		keyCondition.put("key", key);

		Map<String, String> bucketCondition = new HashMap<>();
		bucketCondition.put("bucket", bucket);

		Map<String, String> aclCondition = new HashMap<>();
		aclCondition.put("acl", "private");

		String[] tags = { "starts-with", "$Content-Type", "" };

		conditions.add(keyCondition);
		conditions.add(bucketCondition);
		conditions.add(aclCondition);
		conditions.add(tags);

		PostPolicy policy = new PostPolicy(expiration, conditions);

		// return as a JSON
		return new ObjectMapper().writeValueAsString(policy);
	}

	/**
	 * Encodes to Base64
	 * 
	 * @param postPolicy
	 * @throws UnsupportedEncodingException
	 */
	private String getEncodedPolicyDocument(String postPolicy) throws UnsupportedEncodingException
	{
		return java.util.Base64.getEncoder().encodeToString(postPolicy.getBytes("UTF-8"));
	}

	/**
	 * Signs the policy using the AWS Secret key that was generated for the IAM
	 * User with upload permissions.
	 * 
	 * @param encodedPostPolicy
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 */
	private String getPolicySignature(String encodedPostPolicy)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException
	{
		// Secret key
		String secretAccessKey = System.getenv().get(SecurityUtils.AWS_SECRET_ACCESS_KEY);
		
		// setup HMAC
		Mac shaHMac = Mac.getInstance(SecurityUtils.HMAC_SHA1_ALGORITHM);
		SecretKeySpec secretKey = new SecretKeySpec(secretAccessKey.getBytes("UTF-8"),
				SecurityUtils.HMAC_SHA1_ALGORITHM);
		shaHMac.init(secretKey);

		// encrypt
		byte[] rawHmac = shaHMac.doFinal(encodedPostPolicy.getBytes("UTF-8"));

		// encode encrypted signature
		String signature = new String(Base64.getEncoder().encodeToString(rawHmac));

		return signature;
	}
	
	/**
	 * Response data as JSON
	 * 
	 * @param key
	 * @param encodedPostPolicy
	 * @param signature
	 * @return
	 * @throws JsonProcessingException 
	 */
	private String createResponse(String key, String encodedPostPolicy, String signature) throws JsonProcessingException
	{		
		String accessKey = System.getenv().get(SecurityUtils.AWS_ACCESS_KEY);
		String uploadUrl = System.getenv().get(ENV_KEY_S3_BASE_URL) + "/" +System.getenv().get(ENV_KEY_UPLOAD_VIDEOS_BUCKET);;
		
		S3UploadPolicy s3UploadPolicy = new S3UploadPolicy(signature, encodedPostPolicy, accessKey, uploadUrl, key);
		
		return new ObjectMapper().writeValueAsString(s3UploadPolicy);
	}
}
