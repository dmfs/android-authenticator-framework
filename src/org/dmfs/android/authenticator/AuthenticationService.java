/*
 * Copyright (C) 2013 Marten Gajda <marten@dmfs.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.dmfs.android.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * A service that acts as an authenticator in Android.
 * <p>
 * Add this to your <code>AndroidManifest.xml</code>
 * </p>
 * 
 * <pre>
 *        &lt;service
 *             android:name="org.dmfs.android.authenticator.AuthenticationService"
 *             android:exported="true" >
 *             &lt;intent-filter>
 *                 &lt;action android:name="android.accounts.AccountAuthenticator" />
 *             &lt;/intent-filter>
 * 
 *             &lt;meta-data
 *                 android:name="android.accounts.AccountAuthenticator"
 *                 android:resource="@xml/authenticator" />
 *         &lt;/service>
 * </pre>
 * 
 * You may have to adjust this to your needs (e.g. add <code>android:process</code>, adjust the authenticator resource name).
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AuthenticationService extends Service
{
	private Authenticator mAuthenticator;


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		// get a new authenticator
		mAuthenticator = new Authenticator(this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		// return the authenticator IBinder.
		return mAuthenticator.getIBinder();
	}

}
