package us.shalabh.svt.lambda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class SetObjectPermissionTest
{

	private SNSEvent input;

	@Before
	public void createInput() throws IOException
	{
		// TODO: set up your sample input object here.
		input = TestUtils.parse("/sns-event.json", SNSEvent.class);
	}

	private Context createContext()
	{
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testSetObjectPermission()
	{
		// SetObjectPermission handler = new SetObjectPermission();
		// Context ctx = createContext();

		// String output = handler.handleRequest(input, ctx);

		// TODO: validate output here if needed.
		// Assert.assertEquals("Hello from SNS!", output);

		try (InputStream is = new FileInputStream(
				"D:\\workspaces\\neon\\serverless-video-transcoder\\src\\test\\resources\\sns-s3-event.json");
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));)
		{

			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null)
			{
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			String fileAsString = sb.toString();
			System.out.println("Contents : " + fileAsString);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(fileAsString);
			System.out.println(actualObj.toString());

			// When
			JsonNode jsonNode1 = actualObj.get("Records").get(0).get("s3").get("object").get("key");
			System.out.println(jsonNode1.textValue());

			JsonNode bucketNode = actualObj.get("Records").get(0).get("s3").get("bucket").get("name");
			System.out.println(bucketNode.textValue());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
