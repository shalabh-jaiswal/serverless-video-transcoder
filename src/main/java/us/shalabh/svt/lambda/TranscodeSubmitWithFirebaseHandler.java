package us.shalabh.svt.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.api.core.ApiFuture;
import com.google.firebase.database.DatabaseReference;

import us.shalabh.svt.lambda.model.VideoTranscodingStatus;
import us.shalabh.svt.utils.db.FirebaseUtils;

/**
 * Includes functionality for Firebase connectivity
 *
 * @author Shalabh Jaiswal
 */
public class TranscodeSubmitWithFirebaseHandler implements RequestHandler<S3Event, String>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(TranscodeSubmitWithFirebaseHandler.class.getName());

	// s3
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	// Clients are built using the default credentials provider chain. This
	// will attempt to get your credentials in the following order:
	// 1. Environment variables (AWS_ACCESS_KEY and AWS_SECRET_KEY).
	// 2. Java system properties (AwsCredentials.properties).
	// 3. Instance profile credentials on EC2 instances.
	private AmazonElasticTranscoder amazonElasticTranscoder = AmazonElasticTranscoderClientBuilder.standard().build();

	/**
	 * default constructor
	 */
	public TranscodeSubmitWithFirebaseHandler()
	{
	}

	// Test purpose only.
	TranscodeSubmitWithFirebaseHandler(AmazonS3 s3, AmazonElasticTranscoder amazonElasticTranscoder)
	{
		this.s3 = s3;
		this.amazonElasticTranscoder = amazonElasticTranscoder;
	}

	// This is the ID of the Elastic Transcoder pipeline that was created when
	// setting up your AWS environment
	private static final String ENV_KEY_PIPELINE_ID = "PIPELINE_ID";

	// Presets that will be used to create the videos
	private static final String VID_WEB_720p = "1351620000001-100070";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.
	 * lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public String handleRequest(S3Event event, Context context)
	{
		logger.info("Received event: ");
		
		// Get the object from the event and show its content type
		String bucket = event.getRecords().get(0).getS3().getBucket().getName();
		String key = event.getRecords().get(0).getS3().getObject().getKey();
		
		try
		{
			// we will just get the meta-data instead
			ObjectMetadata metaData = s3.getObjectMetadata(bucket, key);
			String contentType = metaData.getContentType();

			logger.info("CONTENT TYPE:" + contentType);

			// Create Job inputs
			JobInput input = new JobInput().withKey(key);

			// output key
			String decodedKey = key.replaceAll("\\+", " ");
			String outputKey = decodedKey.split("\\.")[0];

			logger.info("INPUT KEY:" + key);
			logger.info("OUTPUT KEY:" + outputKey);

			CreateJobOutput vid_web_720p = new CreateJobOutput().withKey(outputKey + "-web-720p" + ".mp4")
					.withPresetId(VID_WEB_720p);

			List<CreateJobOutput> outputs = Arrays.asList(vid_web_720p);

			// Create the job request and the job.
			String pipelineID = System.getenv().get(ENV_KEY_PIPELINE_ID);
			CreateJobRequest createJobRequest = new CreateJobRequest().withPipelineId(pipelineID).withInput(input)
					.withOutputKeyPrefix(outputKey + "/").withOutputs(outputs);

			amazonElasticTranscoder.createJob(createJobRequest).getJob();

			// update firebase
			pushVideoEntryToFirebase(outputKey);

			logger.info("End Event:");

			return contentType;
		}
		catch (Exception e)
		{			
			// swallow
			logger.error(String.format("Error getting object %s from bucket %s. Make sure they exist and"
					+ " your bucket is in the same region as this function.", bucket, key), e);		
			
			return null;
		}
	}

	/**
	 * updates firebase database TODO: create a separate layer for all DB Access
	 * functions
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void pushVideoEntryToFirebase(String key) throws InterruptedException, ExecutionException 
	{
		// uuid of the key
		String uuid = key.split("_")[0];
		
		logger.info("inserting video entry into firebase for key uuid: " + uuid);

		DatabaseReference database = FirebaseUtils.getDatabaseReference();
		
		// insert into firebase
		ApiFuture<Void> result = database.child("videos").child(uuid).setValueAsync(new VideoTranscodingStatus(true));
		// wait for it to execute
		result.get();			
	}
}