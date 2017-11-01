package us.shalabh.svt.utils.http;

import java.util.HashMap;
import java.util.Map;

import us.shalabh.svt.lambda.model.ServerlessInput;
import us.shalabh.svt.lambda.model.ServerlessOutput;

/**
 * Utilities for Http
 *
 * @author Shalabh Jaiswal
 */
public class HttpUtils
{
	// auth token header name
	public static final String HEADER_AUTHORIZATION = "Authorization";
	// access denied
	public static final int HTTP_ACCESS_DENIED = 401;
	// token prefix
	public static final String TOKEN_PREFIX = "Bearer ";
	// auth0 env variable keys
	public static final String AUTH0_DOMAIN = "AUTH0_DOMAIN";
	public static final String AUTH0_SECRET = "AUTH0_SECRET";
	public static final String AUTH0_CLIENT_ID = "AUTH0_CLIENT_ID";	
	
	/**
	 * Sets CORS headers
	 * 
	 * @param headers
	 * @return
	 */
	public static void setCORSHeaders(ServerlessOutput output)
	{
		Map<String, String> headers = output.getHeaders();
		
		if(headers == null)
		{
			headers = new HashMap<String, String>();
		}
		
		// TODO narrow the access, and pick up the url from a properties file.
		headers.put("Access-Control-Allow-Origin", "*");
		output.setHeaders(headers);		
	}

	/**
	 * extracts the authToken from the input header
	 * 
	 * @param input
	 * @return
	 */
	public static String getAuthorizationToken(ServerlessInput input)
	{
		String rawToken = input.getHeaders().get(HEADER_AUTHORIZATION);
		
		// there is at least something.
		if (rawToken != null && rawToken.length() > 0)
		{
			return rawToken.substring(rawToken.lastIndexOf(TOKEN_PREFIX) + TOKEN_PREFIX.length());
		}

		return null;
	}
	
	/**
	 * sets an error response with 500 code
	 * 
	 * @param output
	 */
	public static void setAccessDeniedResponse(ServerlessOutput output)
	{
		output.setStatusCode(HTTP_ACCESS_DENIED);
		setCORSHeaders(output);
		
		output.setBody("Access Denied");
	}
}
