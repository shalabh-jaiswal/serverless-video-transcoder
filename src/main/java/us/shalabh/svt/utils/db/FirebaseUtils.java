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
package us.shalabh.svt.utils.db;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Firebase DB related utilities
 *
 * @author Shalabh Jaiswal
 */
public class FirebaseUtils
{
	// Initialize the Log4j logger.
	static final Logger logger = LogManager.getLogger(FirebaseUtils.class.getName());

	// re-usable connection
	// TODO this needs to be cleaned up. this is not a good way to reuse a
	// connection
	private static DatabaseReference database = null;

	// env variables related to firebase db
	private static final String ENV_KEY_SERVICE_ACCOUNT = "SERVICE_ACCOUNT";
	private static final String ENV_KEY_DATABASE_URL = "DATABASE_URL";

	/**
	 * initializes Firebase DB instance
	 */
	public static DatabaseReference getDatabaseReference()
	{
		if (database == null)
		{
			// Initialize Firebase
			try (InputStream serviceAccount = FirebaseUtils.class
					.getResourceAsStream("/" + System.getenv().get(ENV_KEY_SERVICE_ACCOUNT));)
			{
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(serviceAccount))
						.setDatabaseUrl(System.getenv().get(ENV_KEY_DATABASE_URL)).build();
				FirebaseApp.initializeApp(options);
			}
			catch (IOException e)
			{
				// swallow for now
				logger.error("ERROR: invalid service account credentials.", e);
			}

			// Shared Database reference
			return FirebaseDatabase.getInstance().getReference();
		}

		// we already have an instance ready
		return database;
	}
}
