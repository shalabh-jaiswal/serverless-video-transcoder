package us.shalabh.svt.lambda;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;

/**
 * Attempts a DoS Attack. <b>Don't forget to throttle the end-point this lambda
 * invokes</b>
 *
 * @author Shalabh Jaiswal
 */
public class DoSAttackSimulator implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(DoSAttackSimulator.class.getName());

	// endpoint
	static final String ENV_KEY_ENDPOINT = "DOS_ENDPOINT";

	// why you say?
	int i = 0;

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
		context.getLogger().log("Input: " + input);

		attemptAttack();

		return null;
	}

	/**
	 * attempts a DoS Attack
	 */
	private void attemptAttack()
	{
		// endpoint
		String endpoint = System.getenv().get(ENV_KEY_ENDPOINT);

		ExecutorService taskExecutor = Executors.newFixedThreadPool(20);

		for (; i < 200; i++)
		{
			// HA! lambda in a lambda
			taskExecutor.execute(() -> {
				try
				{
					// Java URL connection
					URL url = new URL(endpoint);
					URLConnection conn = url.openConnection();
					
					conn.getInputStream();
					logger.info("Successful Response Processed for iteration: " + i);
				}
				catch (Exception e)
				{
					// swallow again and continue
					e.printStackTrace();
				}
			});
		}

		// done
		taskExecutor.shutdown();

		try
		{
			// wait for all to finish
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e)
		{
			// swallow
			e.printStackTrace();
		}

	}
}
