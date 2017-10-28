package us.shalabh.svt.lambda;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Obtains meta-data of the video and places them in the transcoded s3 bucket
 * 
 * @author sj
 *
 */
public class CreateObjectMetadata implements RequestHandler<SNSEvent, String>
{
	// s3 object
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	// meta-data location
	public static final String TMP_DIR = "/tmp/";
	public static final String META_DATA_FILE_SUFFIX = "_meta-data.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.
	 * lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public String handleRequest(SNSEvent event, Context context)
	{
		context.getLogger().log("Create Meta-Data Start...");

		// sns message
		String message = event.getRecords().get(0).getSNS().getMessage();

		try
		{
			// jackson json mapper
			ObjectMapper mapper = new ObjectMapper();
			JsonNode snsEventJson = mapper.readTree(message);

			// decoded object key from sns notification
			JsonNode jsonS3Key = snsEventJson.get("Records").get(0).get("s3").get("object").get("key");
			String key = jsonS3Key.textValue().replaceAll("\\+", " ");

			// bucket name from sns notification
			String bucket = snsEventJson.get("Records").get(0).get("s3").get("bucket").get("name").textValue();

			context.getLogger().log("Bucket/Key: " + bucket + "/" + key);

			// get the meta-data, write it to a file and put it on S3
			// note we are not deleting the file from /tmp
			// TODO how does lambda cleanup /tmp ? will it persist for ever ?
			String metaData = getMetaData(s3, bucket, key);
			writeMetaDataToFileSystem(bucket, key, metaData);
			putMetaDataToS3(context, s3, bucket, key, metaData);

		}
		catch (Exception e)
		{
			context.getLogger().log("Error Putting Meta-Data");
			e.printStackTrace();

			throw new RuntimeException(e);
		}

		context.getLogger().log("Create Meta-Data End.");

		return message;
	}

	/**
	 * we are just going to get the S3 Object meta-data instead of the video
	 * meta-data. we are not going the EC2/FFProbe route.
	 * 
	 * @param s3
	 * @param bucket
	 * @param key
	 * @return
	 */
	private String getMetaData(AmazonS3 s3, String bucket, String key)
	{
		ObjectMetadata s3MetaData = s3.getObjectMetadata(bucket, key);

		StringBuilder metaDataBuilder = new StringBuilder();
		metaDataBuilder.append("Content Type: ").append(s3MetaData.getContentType()).append("\n");
		metaDataBuilder.append("Cache Control: ").append(s3MetaData.getCacheControl()).append("\n");

		return metaDataBuilder.toString();
	}

	/**
	 * write the meta-data to lambdas tmp file system.
	 * 
	 * @param metaData
	 * @throws IOException
	 */
	private void writeMetaDataToFileSystem(String bucket, String key, String metaData) throws IOException
	{
		String metaDataFileName = TMP_DIR + key + META_DATA_FILE_SUFFIX;

		Files.write(Paths.get(metaDataFileName), metaData.getBytes());
	}

	/**
	 * Puts the meta-data to the S3 bucket as an object
	 * 
	 * @param s3
	 * @param metaData
	 * @return
	 */
	private void putMetaDataToS3(Context context, AmazonS3 s3, String bucket, String key, String metaData)
	{
		try
		{
			String metaDataFileName = key + META_DATA_FILE_SUFFIX;
			File file = new File(TMP_DIR + metaDataFileName);
			s3.putObject(new PutObjectRequest(bucket, metaDataFileName, file));
			
			context.getLogger().log("Put Object: " +metaDataFileName +" to bucket: " +bucket +" on s3");
		}
		catch (AmazonServiceException ase)
		{
			context.getLogger().log("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			context.getLogger().log("Error Message:    " + ase.getMessage());
			context.getLogger().log("HTTP Status Code: " + ase.getStatusCode());
			context.getLogger().log("AWS Error Code:   " + ase.getErrorCode());
			context.getLogger().log("Error Type:       " + ase.getErrorType());
			context.getLogger().log("Request ID:       " + ase.getRequestId());
		}
		catch (AmazonClientException ace)
		{
			context.getLogger()
					.log("Caught an AmazonClientException, which means the client encountered "
							+ "an internal error while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			context.getLogger().log("Error Message: " + ace.getMessage());
		}
	}
}
