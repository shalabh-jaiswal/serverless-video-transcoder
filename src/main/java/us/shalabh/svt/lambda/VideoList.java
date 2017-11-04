package us.shalabh.svt.lambda;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;
import us.shalabh.svt.lambda.model.SimpleS3Object;
import us.shalabh.svt.utils.http.HttpUtils;

/**
 * Video List implemented in Java. <b>No callback hell here</b>
 *
 * @author Shalabh Jaiswal
 */
public class VideoList implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(VideoList.class.getName());

	// bucket name env key
	static final String ENV_KEY_BUCKET_NAME = "TRANSCODED_VIDEOS_BUCKET";

	// construct S3 object
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

	// default constructor
	public VideoList()
	{
	}

	// Test purpose only.
	VideoList(AmazonS3 s3)
	{
		this.s3 = s3;
	}

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
			// get s3 object then convert to json
			List<SimpleS3Object> s3Objects = getVideoListFromBucket();
			String s3ObjectsJSON = toJSON(s3Objects);

			logger.info("S3 Objects: " + s3ObjectsJSON);

			// response
			HttpUtils.setCORSHeaders(output);
			output.setBody(s3ObjectsJSON);
		}
		catch (Exception e)
		{
			// set error response
			HttpUtils.setInternalServerErrorResponse(output);
		}

		return output;
	}

	/**
	 * get the list of videos in the given bucket from s3
	 * 
	 * @return
	 */
	private List<SimpleS3Object> getVideoListFromBucket()
	{
		String bucket = System.getenv().get(ENV_KEY_BUCKET_NAME);

		ObjectListing objectListing = null;

		List<SimpleS3Object> objects = new ArrayList<>();
		SimpleS3Object simpleS3Object;

		// haven't written a do while in a while :) !!!
		do
		{
			objectListing = s3.listObjects(bucket);

			// get all the objects
			for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries())
			{
				simpleS3Object = new SimpleS3Object();
				String key = s3ObjectSummary.getKey();

				// we only want the videos not their meta-data files.
				if (key != null && key.endsWith(".mp4"))
				{
					simpleS3Object.setKey(key);
					// TODO encode url (' ' into +)
					simpleS3Object.setUrl(s3.getUrl(bucket, key).toString());

					objects.add(simpleS3Object);
				}
			}
		} while (objectListing != null && objectListing.isTruncated() == true);

		return objects;
	}

	/**
	 * Convert S3Object list to JSON
	 * 
	 * @param s3Objects
	 * @return
	 */
	private String toJSON(List<SimpleS3Object> s3Objects)
	{
		ObjectMapper mapper = new ObjectMapper();

		// Set pretty printing of json
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try
		{
			return mapper.writeValueAsString(s3Objects);
		}
		catch (JsonProcessingException e)
		{
			// TODO caller should set 500 status code in response
			logger.error("Error Converting S3Objects List to JSON", e);
			throw new RuntimeException(e);
		}
	}
}
