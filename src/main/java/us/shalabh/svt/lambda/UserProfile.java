package us.shalabh.svt.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;

public class UserProfile implements RequestHandler<ServerlessInput, ServerlessOutput> {

    @Override
    public ServerlessOutput handleRequest(ServerlessInput input, Context context) {
        context.getLogger().log("Input: " + input);

        // TODO: implement your handler
        ServerlessOutput output = new ServerlessOutput();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        output.setHeaders(headers);
        output.setBody("{\"message\":\"Hello from Lamby\"}");
        
        context.getLogger().log("Output: " +output);
        
        return output;
    }

}
