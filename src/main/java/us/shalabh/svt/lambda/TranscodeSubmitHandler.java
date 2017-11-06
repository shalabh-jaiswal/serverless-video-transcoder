package us.shalabh.svt.lambda;

import java.util.Arrays;
import java.util.List;

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

/**
 * AWS Lambda function to submit a Video Transcode job. 
 *
 * @author Shalabh Jaiswal
 */
public class TranscodeSubmitHandler implements RequestHandler<S3Event, String>
{
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
	public TranscodeSubmitHandler()
	{
	}

	// Test purpose only.
	TranscodeSubmitHandler(AmazonS3 s3, AmazonElasticTranscoder amazonElasticTranscoder)
	{
		this.s3 = s3;
		this.amazonElasticTranscoder = amazonElasticTranscoder;
	}

	// This is the ID of the Elastic Transcoder pipeline that was created when
	// setting up your AWS environment
	private static final String ENV_KEY_PIPELINE_ID = "PIPELINE_ID";

	// Presets that will be used to create the videos
	private static final String VID_1080p = "1351620000001-000001";
	private static final String VID_720p = "1351620000001-000010";
	private static final String VID_WEB_720p = "1351620000001-100070";	

	@Override
	public String handleRequest(S3Event event, Context context)
	{
		context.getLogger().log("Received event: ");
		
		// Get the object from the event and show its content type
		String bucket = event.getRecords().get(0).getS3().getBucket().getName();
		String key = event.getRecords().get(0).getS3().getObject().getKey();
		
		try
		{
			// we will just get the meta-data instead
			ObjectMetadata metaData = s3.getObjectMetadata(bucket, key);
			String contentType = metaData.getContentType();

			context.getLogger().log("CONTENT TYPE:" + contentType);

			// Create Job inputs
			JobInput input = new JobInput().withKey(key);

			// output key
			String decodedKey = key.replaceAll("\\+", " ");
			String outputKey = decodedKey.split("\\.")[0];

			context.getLogger().log("INPUT KEY:" + key);
			context.getLogger().log("OUTPUT KEY:" + outputKey);
			
			// Setup the job outputs using the HLS presets.
			CreateJobOutput vid_1080p = new CreateJobOutput().withKey(outputKey + "-1080p" + ".mp4")
					.withPresetId(VID_1080p);

			CreateJobOutput vid_720p = new CreateJobOutput().withKey(outputKey + "-720p" + ".mp4")
					.withPresetId(VID_720p);

			CreateJobOutput vid_web_720p = new CreateJobOutput().withKey(outputKey + "-web-720p" + ".mp4")
					.withPresetId(VID_WEB_720p);

			List<CreateJobOutput> outputs = Arrays.asList(vid_1080p, vid_720p, vid_web_720p);

			// Create the job request and the job.
			String pipelineID = System.getenv().get(ENV_KEY_PIPELINE_ID);
			CreateJobRequest createJobRequest = new CreateJobRequest().withPipelineId(pipelineID).withInput(input)
					.withOutputKeyPrefix(outputKey + "/").withOutputs(outputs);

			amazonElasticTranscoder.createJob(createJobRequest).getJob();

			context.getLogger().log("End Event:");
			
			return contentType;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			context.getLogger().log(String.format("Error getting object %s from bucket %s. Make sure they exist and"
					+ " your bucket is in the same region as this function.", bucket, key));
			throw e;
		}
	}
}