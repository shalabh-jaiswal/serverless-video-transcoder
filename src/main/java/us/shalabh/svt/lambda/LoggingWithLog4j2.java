package us.shalabh.svt.lambda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;
import us.shalabh.svt.utils.http.HttpUtils;

/**
 * Logs statements using log4j2. Triggered from Lambda Console Test
 *
 * @author Shalabh Jaiswal
 */
public class LoggingWithLog4j2 implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Initialize the Log4j logger.
    static final Logger logger = LogManager.getLogger(LoggingWithLog4j2.class.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public ServerlessOutput handleRequest(ServerlessInput input, Context context)
	{
		context.getLogger().log("Inside LoggingWithLog4j2");
		
		logger.info("Input: " +input);
		logger.warn("This is a warning");
		logger.error("This is an error");
		logger.error("This is an error with Stack Trace: ", new RuntimeException("Exception Occured"));
		
		// output
		ServerlessOutput output = new ServerlessOutput();
		HttpUtils.setCORSHeaders(output);
		return output;
	}

}
