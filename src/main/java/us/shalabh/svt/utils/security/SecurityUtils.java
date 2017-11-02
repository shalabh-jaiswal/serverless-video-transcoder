package us.shalabh.svt.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import us.shalabh.svt.utils.http.HttpUtils;

/**
 * Security Utils
 *
 * @author Shalabh Jaiswal
 */
public class SecurityUtils
{

	/**
	 * Decodes a JWT token
	 * 
	 * @param authToken
	 * @return
	 */
	public static DecodedJWT decodeJWTToken(String authToken) throws Exception
	{
		// auth0 secret set as env variable in the lambda settings.
		String secret = System.getenv().get(HttpUtils.AUTH0_SECRET);

		Algorithm algorithm = Algorithm.HMAC256(secret);

		// Reusable verifier instance
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(authToken);

		// no exception; token must be good.
		return decodedJWT;
	}
}
