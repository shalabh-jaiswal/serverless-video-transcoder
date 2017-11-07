/** 
 * Copyright 2017. All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of shalabh.us. The intellectual and technical 
 * concepts contained herein are proprietary to shalabh.us 
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from shalabh.us
 */
package us.shalabh.svt.lambda.model;

/**
 * Requests for Signed Urls
 *
 * @author Shalabh Jaiswal
 */
public class SignedUrlRequest
{
	// firebase id
	private String firebaseId;
	
	// key
	private String key;

	/**
	 * @return the firebaseId
	 */
	public String getFirebaseId()
	{
		return firebaseId;
	}

	/**
	 * @param firebaseId the firebaseId to set
	 */
	public void setFirebaseId(String firebaseId)
	{
		this.firebaseId = firebaseId;
	}

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SignedUrlRequest [firebaseId=");
		builder.append(firebaseId);
		builder.append(", key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}
}
