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
 * Status of a video being transcoded
 *
 * @author Shalabh Jaiswal
 */
public class VideoTranscodingStatus
{
	// is video still transcoding
	private boolean transcoding;
	
	/**
	 * @param transcoding
	 */
	public VideoTranscodingStatus(boolean transcoding)
	{
		super();
		this.transcoding = transcoding;
	}

	/**
	 * @return the transcoding
	 */
	public boolean isTranscoding()
	{
		return transcoding;
	}

	/**
	 * @param transcoding the transcoding to set
	 */
	public void setTranscoding(boolean transcoding)
	{
		this.transcoding = transcoding;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("VideoTranscodingStatus [transcoding=");
		builder.append(transcoding);
		builder.append("]");
		return builder.toString();
	}
}
