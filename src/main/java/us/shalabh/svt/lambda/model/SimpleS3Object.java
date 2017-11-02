package us.shalabh.svt.lambda.model;

/**
 * A simplified S3 Object
 *
 * @author Shalabh Jaiswal
 */
public class SimpleS3Object
{
	// key
	private String key;
	
	// url
	private String url;

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
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleS3Object [key=");
		builder.append(key);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}
}
