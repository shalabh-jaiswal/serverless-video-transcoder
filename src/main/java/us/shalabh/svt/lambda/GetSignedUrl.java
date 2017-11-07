package us.shalabh.svt.lambda;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;
import us.shalabh.svt.lambda.model.SignedUrlRequest;
import us.shalabh.svt.lambda.model.SimpleS3Object;
import us.shalabh.svt.utils.http.HttpUtils;

/**
 * Gets Signed URLs for the videos
 *
 * @author Shalabh Jaiswal
 */
public class GetSignedUrl implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(GetSignedUrl.class.getName());

	// bucket name env key
	private static final String ENV_KEY_BUCKET_NAME = "TRANSCODED_VIDEOS_BUCKET";

	// construct S3 object
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

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

		String bodyJson = input.getBody();

		ServerlessOutput output = new ServerlessOutput();

		try
		{
			// get the signed url and set it in the body
			String signedUrl = getSignedUrl(bodyJson);

			HttpUtils.setCORSHeaders(output);
			output.setBody(signedUrl);
		}
		catch (Exception e)
		{
			logger.error("Error getting Signed URL", e);
			// set error response
			HttpUtils.setInternalServerErrorResponse(output);
		}

		logger.info("Output: " + output);

		return output;
	}

	/**
	 * Generates a signed url valid for 1 hour.
	 * 
	 * @param bucket
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	private String getSignedUrl(String bodyJson) throws IOException
	{
		String bucket = System.getenv().get(ENV_KEY_BUCKET_NAME);

		ObjectMapper mapper = new ObjectMapper();
		SignedUrlRequest[] request = mapper.readValue(bodyJson, SignedUrlRequest[].class);

		java.util.Date expiration = (new java.util.Date());
		long msec = expiration.getTime();
		// expires in 1 hour.
		msec += 1000 * 60 * 60;
		expiration.setTime(msec);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = null;
		List<SimpleS3Object> s3Objects = new ArrayList<>();

		// for each requested key generate a signed url only if the key is not null.
		for (int i = 0; i < request.length; i++)
		{
			if (request[i].getKey() != null)
			{
				// generate the signed url
				generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, request[i].getKey());
				generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
				generatePresignedUrlRequest.setExpiration(expiration);

				URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);

				// return as json
				SimpleS3Object s3Object = new SimpleS3Object();
				s3Object.setKey(request[i].getKey());
				s3Object.setUrl(url.toString());
				
				s3Objects.add(s3Object);
			}
		}

		return mapper.writeValueAsString(s3Objects);
	}
}
