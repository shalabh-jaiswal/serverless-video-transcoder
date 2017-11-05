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

import java.util.List;

/**
 * A Post Policy for uploding to S3
 *
 * @author Shalabh Jaiswal
 */
public class PostPolicy
{
	// expiration
	private String expiration;

	// conditions
	private List<Object> conditions;

	/**
	 * @param expiration
	 * @param conditions
	 */
	public PostPolicy(String expiration, List<Object> conditions)
	{
		super();
		this.expiration = expiration;
		this.conditions = conditions;
	}

	/**
	 * default constructor
	 */
	public PostPolicy()
	{
	}
	
	/**
	 * @return the expiration
	 */
	public String getExpiration()
	{
		return expiration;
	}

	/**
	 * @param expiration
	 *            the expiration to set
	 */
	public void setExpiration(String expiration)
	{
		this.expiration = expiration;
	}

	/**
	 * @return the conditions
	 */
	public List<Object> getConditions()
	{
		return conditions;
	}

	/**
	 * @param conditions
	 *            the conditions to set
	 */
	public void setConditions(List<Object> conditions)
	{
		this.conditions = conditions;
	}

}
