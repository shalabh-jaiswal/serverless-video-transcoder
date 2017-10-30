package us.shalabh.svt.lambda;

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
        output.setBody("Hello from Lamby");
        
        return output;
    }

}
