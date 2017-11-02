package us.shalabh.svt.lambda;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import us.shalabh.svt.lambda.model.AuthPolicy;
import us.shalabh.svt.lambda.model.TokenAuthorizerContext;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class CustomAuthorizerTest {

    private static TokenAuthorizerContext input;

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
    public void testCustomAuthorizer() {
        CustomAuthorizer handler = new CustomAuthorizer();
        Context ctx = createContext();

        AuthPolicy output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
