package us.shalabh.svt.lambda;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.firebase.database.DatabaseReference;

import us.shalabh.svt.lambda.model.VideoTranscodingStatus;
import us.shalabh.svt.utils.db.FirebaseUtils;

/**
 * Updates the status of the transcoded video in the DB (Firebase)
 *
 * @author Shalabh Jaiswal
 */
public class TranscodingStatusUpdate implements RequestHandler<SNSEvent, String>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(TranscodingStatusUpdate.class.getName());

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
		logger.info("Received event: " + event);
		String message = event.getRecords().get(0).getSNS().getMessage();

		logger.info("From SNS: " + message);

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

			logger.info("Bucket/Key: " + bucket + "/" + objectKey);
			
			updateDB(bucket, objectKey);
		}
		catch (Exception e)
		{
			// swallow for now.
			// TODO put they key in a DLQ so it can be notified and the issue
			// inspected
			logger.error("Error Trying to update db with transcoding status", e);
		}
		return message;
	}
	
	/**
	 * updates the status in Firebase DB
	 * 
	 * @param bucket
	 * @param key
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void updateDB(String bucket, String key) throws InterruptedException, ExecutionException
	{
		// uuid of the key
		String uuid = key.split("_")[0];
		
		// status
		VideoTranscodingStatus status = new VideoTranscodingStatus(false, key, bucket);
		
		// update the db
		DatabaseReference database = FirebaseUtils.getDatabaseReference();
		ApiFuture<Void> result = database.child("videos").child(uuid).setValueAsync(status);
		
		// wait for it
		result.get();
	}
}
