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
	private static final String HEADER_AUTHORIZATION = "Authorization";
	// token prefix
	private static final String TOKEN_PREFIX = "Bearer ";
	
	// HTTP Status Codes
	private static final int HTTP_ACCESS_DENIED = 401;
	private static final int HTTP_INTERNAL_SERVER_ERROR = 500;
	
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
		return extractAuthorizationToken(input.getHeaders().get(HEADER_AUTHORIZATION));
	}
	
	/**
	 * Extracts the auth token from the row token.
	 * raw token in our applications is prefixed with: 'Bearer '
	 * 
	 * @param rawToken
	 * @return
	 */
	public static String extractAuthorizationToken(String rawToken)
	{
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
	
	/**
	 * sets an error response with 500 code
	 * 
	 * @param output
	 */
	public static void setInternalServerErrorResponse(ServerlessOutput output)
	{
		output.setStatusCode(HTTP_INTERNAL_SERVER_ERROR);
		setCORSHeaders(output);
		
		output.setBody("Internal Server Error");
	}	
}
