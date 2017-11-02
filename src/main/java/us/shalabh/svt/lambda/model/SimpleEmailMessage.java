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
 * A Simple Email Message Wrapper
 *
 * @author Shalabh Jaiswal
 */
public class SimpleEmailMessage
{
	// from
	private String from;
	
	// to list
	private List<String> to;
	
	// subject
	private String subject;
	
	// body
	private String body;

	/**
	 * @return the from
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public List<String> getTo()
	{
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(List<String> to)
	{
		this.to = to;
	}

	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleEmailMessage [from=");
		builder.append(from);
		builder.append(", to=");
		builder.append(to);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", body=");
		builder.append(body);
		builder.append("]");
		return builder.toString();
	}
}
