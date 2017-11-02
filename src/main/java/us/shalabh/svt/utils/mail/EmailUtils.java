package us.shalabh.svt.utils.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import us.shalabh.svt.lambda.model.SimpleEmailMessage;

/**
 * Utility class for sending emails. Leverages AWS SES.
 *
 * @author Shalabh Jaiswal
 */
public class EmailUtils
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(EmailUtils.class.getName());

	/**
	 * Sends email leveraging AWS SES
	 * 
	 * @param simpleEmailMessage
	 */
	public void send(SimpleEmailMessage simpleEmailMessage)
	{
		try
		{
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().build();

			// build the SES object
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(simpleEmailMessage.getTo()))
					.withMessage(new Message()
							.withBody(new Body()
									.withHtml(new Content().withCharset("UTF-8").withData(simpleEmailMessage.getBody())))
							.withSubject(new Content().withCharset("UTF-8").withData(simpleEmailMessage.getSubject())))
					.withSource(simpleEmailMessage.getFrom());

			client.sendEmail(request);			
			
			logger.info("Email sent!");
		}
		catch (Exception ex)
		{
			logger.error("The email was not sent. Error message: ", ex);

			// re-throw
			throw new RuntimeException(ex);
		}
	}
}
