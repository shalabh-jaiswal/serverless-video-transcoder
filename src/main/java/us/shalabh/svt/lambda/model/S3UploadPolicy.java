package us.shalabh.svt.lambda.model;

/**
 * Response containing signed S3 Upload Policy 
 *
 * @author Shalabh Jaiswal
 */
public class S3UploadPolicy
{
	// signature
	private String signature;
	
	// encoded policy
	private String encodedPolicy;
	
	// public access key
	private String accessKey;
	
	// upload url
	private String uploadUrl;
	
	// key
	private String key;
	
	/**
	 * @param signature
	 * @param encodedPolicy
	 * @param accessKey
	 * @param uploadUrl
	 * @param key
	 */
	public S3UploadPolicy(String signature, String encodedPolicy, String accessKey, String uploadUrl, String key)
	{
		super();
		this.signature = signature;
		this.encodedPolicy = encodedPolicy;
		this.accessKey = accessKey;
		this.uploadUrl = uploadUrl;
		this.key = key;
	}

	/**
	 * default constructor
	 */
	public S3UploadPolicy()
	{
	}
	
	/**
	 * @return the signature
	 */
	public String getSignature()
	{
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	/**
	 * @return the encodedPolicy
	 */
	public String getEncodedPolicy()
	{
		return encodedPolicy;
	}

	/**
	 * @param encodedPolicy the encodedPolicy to set
	 */
	public void setEncodedPolicy(String encodedPolicy)
	{
		this.encodedPolicy = encodedPolicy;
	}

	/**
	 * @return the accessKey
	 */
	public String getAccessKey()
	{
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey)
	{
		this.accessKey = accessKey;
	}

	/**
	 * @return the uploadUrl
	 */
	public String getUploadUrl()
	{
		return uploadUrl;
	}

	/**
	 * @param uploadUrl the uploadUrl to set
	 */
	public void setUploadUrl(String uploadUrl)
	{
		this.uploadUrl = uploadUrl;
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
		builder.append("S3UploadPolicy [signature=");
		builder.append(signature);
		builder.append(", encodedPolicy=");
		builder.append(encodedPolicy);
		builder.append(", accessKey=");
		builder.append(accessKey);
		builder.append(", uploadUrl=");
		builder.append(uploadUrl);
		builder.append(", key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}	
}
