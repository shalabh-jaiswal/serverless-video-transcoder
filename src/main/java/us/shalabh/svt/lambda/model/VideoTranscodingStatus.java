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
	
	// key
	private String key;
	
	// bucket
	private String bucket;
	
	/**
	 * @param transcoding
	 */
	public VideoTranscodingStatus(boolean transcoding)
	{
		super();
		this.transcoding = transcoding;
	}
	
	/**
	 * @param transcoding
	 * @param key
	 * @param bucket
	 */
	public VideoTranscodingStatus(boolean transcoding, String key, String bucket)
	{
		super();
		this.transcoding = transcoding;
		this.key = key;
		this.bucket = bucket;
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

	/**
	 * @return the bucket
	 */
	public String getBucket()
	{
		return bucket;
	}

	/**
	 * @param bucket the bucket to set
	 */
	public void setBucket(String bucket)
	{
		this.bucket = bucket;
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
		builder.append(", key=");
		builder.append(key);
		builder.append(", bucket=");
		builder.append(bucket);
		builder.append("]");
		return builder.toString();
	}
	
}
