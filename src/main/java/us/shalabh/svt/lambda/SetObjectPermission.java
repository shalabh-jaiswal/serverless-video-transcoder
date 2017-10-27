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
	// s3 object
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	@Override
	public String handleRequest(SNSEvent event, Context context)
	{
		context.getLogger().log("Received event: " + event);
		String message = event.getRecords().get(0).getSNS().getMessage();
		
		try
		{
			// jackson json mapper
			ObjectMapper mapper = new ObjectMapper();
			JsonNode snsEventJson = mapper.readTree(message);

			// decoded object key from sns notification
			JsonNode jsonS3Key = snsEventJson.get("Records").get(0).get("s3").get("object").get("key");
			String objectKey = jsonS3Key.textValue().replaceAll("\\+", " ");
			
			// bucket name from sns notification
			String bucket = snsEventJson.get("Records").get(0).get("s3").get("bucket").get("name").textValue();			
			 						
			context.getLogger().log("Bucket/Key: " + bucket +"/" +objectKey);			
			
			// set the s3 objects ACL.
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
