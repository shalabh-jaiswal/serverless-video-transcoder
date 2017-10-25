package us.shalabh.svt.lambda;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class TranscodeSubmitHandlerTest
{

	private final String CONTENT_TYPE = "image/jpeg";
	private S3Event event;

	@Mock
	private AmazonS3 s3Client;
	
	@Mock
	private S3Object s3Object;
	
	@Mock
	private AmazonElasticTranscoder amazonElasticTranscoder;

	@Captor
	private ArgumentCaptor<GetObjectRequest> getObjectRequest;

	@Before
	public void setUp() throws IOException
	{
		event = TestUtils.parse("/s3-event.put.json", S3Event.class);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(CONTENT_TYPE);
		when(s3Client.getObjectMetadata(any(), any())).thenReturn(objectMetadata);
		
		CreateJobResult createJobResult = new CreateJobResult();
		when(amazonElasticTranscoder.createJob(any())).thenReturn(createJobResult);
	}

	private Context createContext()
	{
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testLambdaFunctionHandler()
	{
		TranscodeSubmitHandler handler = new TranscodeSubmitHandler(s3Client, amazonElasticTranscoder);
		Context ctx = createContext();

		String output = handler.handleRequest(event, ctx);

		// TODO: validate output here if needed.
		Assert.assertEquals(CONTENT_TYPE, output);
	}
}
