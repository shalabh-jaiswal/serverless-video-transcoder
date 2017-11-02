package us.shalabh.svt.lambda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;
import us.shalabh.svt.utils.http.HttpUtils;

/**
 * Validates JWT Token and Obtains User Info from Auth0
 * 
 * TODO clean up exception handling. exceptions are being swallowed, that is bad
 *
 * @author Shalabh Jaiswal
 */
public class UserProfile implements RequestHandler<ServerlessInput, ServerlessOutput>
{
	// Context made global to the class
	private Context context = null;

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
		this.context = context;

		String authToken = HttpUtils.getAuthorizationToken(input);

		// validate token
		boolean isTokenValid = isTokenValid(authToken);
		context.getLogger().log("Token Valid? " + isTokenValid);

		ServerlessOutput output = new ServerlessOutput();

		if (!isTokenValid)
		{
			// Access is denied
			HttpUtils.setAccessDeniedResponse(output);
			return output;
		}

		// Access allowed
		HttpUtils.setCORSHeaders(output);
		output.setBody(getUserInfo(authToken));

		return output;
	}

	/**
	 * Validates the JWT token
	 * 
	 * @param authToken
	 * @return
	 */
	private boolean isTokenValid(String authToken)
	{
		try
		{
			// auth0 secret set as env variable in the lambda settings.
			String secret = System.getenv().get(HttpUtils.AUTH0_SECRET);

			Algorithm algorithm = Algorithm.HMAC256(secret);

			// Reusable verifier instance
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(authToken);
		}
		catch (UnsupportedEncodingException exception)
		{
			// UTF-8 encoding not supported
			context.getLogger().log("UTF-8 encoding not supported");
			exception.printStackTrace();

			return false;
		}
		catch (JWTVerificationException exception)
		{
			// Invalid signature/claims
			context.getLogger().log("Invalid Signature/Claims");
			exception.printStackTrace();

			return false;
		}
		catch (Exception e)
		{
			// other exception
			context.getLogger().log("Invalid Token");
			e.printStackTrace();

			return false;
		}

		// no exception; token must be good.
		return true;
	}

	/**
	 * Gets the user info from the authToken by reaching out to Auth0
	 * 
	 * @param authToken
	 */
	private String getUserInfo(String authToken)
	{
		// auth0 domain
		String domain = System.getenv().get(HttpUtils.AUTH0_DOMAIN);

		try
		{
			// Auth0 url to get User Info
			String auth0Url = "https://" + domain + "/tokeninfo?id_token=" + authToken;

			// Java URL connection
			URL url = new URL(auth0Url);
			URLConnection conn = url.openConnection();
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())))
			{
				String response = in.lines().collect(Collectors.joining());
				return response;
			}
		}
		catch (Exception e)
		{
			context.getLogger().log("Error calling tokeninfo");
			e.printStackTrace();			
		}

		// no user info extracted
		return "Error";
	}
}
