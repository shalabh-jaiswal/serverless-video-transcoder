package us.shalabh.svt.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import us.shalabh.svt.lambda.model.AuthPolicy;
import us.shalabh.svt.lambda.model.TokenAuthorizerContext;
import us.shalabh.svt.utils.http.HttpUtils;
import us.shalabh.svt.utils.security.SecurityUtils;

/**
 * Custom Authorizer
 *
 * @author Shalabh Jaiswal
 */
public class CustomAuthorizer implements RequestHandler<TokenAuthorizerContext, AuthPolicy>
{

	/* (non-Javadoc)
	 * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public AuthPolicy handleRequest(TokenAuthorizerContext input, Context context)
	{
		context.getLogger().log("Input: " + input);

		try
		{
			SecurityUtils.decodeJWTToken(HttpUtils.extractAuthorizationToken(input.getAuthorizationToken()));
		}
		catch (Exception e)
		{
			context.getLogger().log("Authorization Failed. Returning a Deny Auth Policy");
			e.printStackTrace();

			// access denied
			return createAuthPolicy(input, false);
		}

		// authenticated user; create an 'allow' Auth Policy
		context.getLogger().log("Authorization Succeeded. Returning an Allow Auth Policy");
		return createAuthPolicy(input, true);
	}

	/**
	 * Creates an Allow IAM Policy
	 * 
	 * @param input
	 * @return
	 */
	private AuthPolicy createAuthPolicy(TokenAuthorizerContext input, boolean isAllowed)
	{
		// extract data
		String methodArn = input.getMethodArn();
		String[] arnPartials = methodArn.split(":");
		String region = arnPartials[3];
		String awsAccountId = arnPartials[4];
		String[] apiGatewayArnPartials = arnPartials[5].split("/");
		String restApiId = apiGatewayArnPartials[0];
		String stage = apiGatewayArnPartials[1];

		if (!isAllowed)
		{
			// no access
			return new AuthPolicy("user",
					AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage));
		}

		// allows access
		return new AuthPolicy("user",
				AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage));
	}
}
