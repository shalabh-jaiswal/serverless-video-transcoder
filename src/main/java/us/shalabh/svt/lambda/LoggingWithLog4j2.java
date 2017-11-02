package us.shalabh.svt.lambda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

/**
 * Logs statements using log4j2. Triggered from Lambda Console Test
 *
 * @author Shalabh Jaiswal
 */
public class LoggingWithLog4j2 implements RequestHandler<S3Event, String>
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
	public String handleRequest(S3Event input, Context context)
	{
		context.getLogger().log("Inside LoggingWithLog4j2");
		
		logger.info("Input: " +input);
		logger.warn("This is a warning");
		logger.error("This is an error");
		logger.error("This is an error with Stack Trace: ", new RuntimeException("Exception Occured"));
		
		// output
		return "Success";
	}

}
