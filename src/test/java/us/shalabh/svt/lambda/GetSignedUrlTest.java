package us.shalabh.svt.lambda;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class GetSignedUrlTest {

    private static ServerlessInput input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testGetSignedUrl() {
        GetSignedUrl handler = new GetSignedUrl();
        Context ctx = createContext();

        ServerlessOutput output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
