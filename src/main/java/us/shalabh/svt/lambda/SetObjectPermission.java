package us.shalabh.svt.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sets Permissions of Videos once Transcoding is complete.
 *
 * @author Shalabh Jaiswal
 */
public class SetObjectPermission implements RequestHandler<SNSEvent, String>
{

	@Override
	public String handleRequest(SNSEvent event, Context context)
	{
		context.getLogger().log("Received event: " + event);
		String message = event.getRecords().get(0).getSNS().getMessage();
		
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj;			
			actualObj = mapper.readTree(message);

			JsonNode jsonS3Key = actualObj.get("Records").get(0).get("s3").get("object").get("key");

			// decoded key
			String key = jsonS3Key.textValue();
			String objectKey = key.replaceAll("\\+", " ");			

			JsonNode bucketNode = actualObj.get("Records").get(0).get("s3").get("bucket").get("name");			
			String bucket = bucketNode.textValue();
						
			context.getLogger().log("Bucket/Key: " + bucket +"/" +objectKey);			
			
			// create an S3 object and set its acl			
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
			s3.setObjectAcl(bucket, objectKey, CannedAccessControlList.PublicRead);
		}
		catch (Exception e)
		{
			context.getLogger().log("Error handing SNS event");
			e.printStackTrace();

			throw new RuntimeException(e);
		}
		
		return message;
	}
}
