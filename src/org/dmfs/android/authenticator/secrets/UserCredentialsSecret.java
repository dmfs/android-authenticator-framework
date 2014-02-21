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

package org.dmfs.android.authenticator.secrets;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * A protected secret that stores a user's credentials.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class UserCredentialsSecret extends StoredSecret
{

	private final static String SCHEME = "user_creds_secret";

	/**
	 * The username.
	 */
	private String mUsername;

	/**
	 * The password.
	 */
	private String mPassword;

	/**
	 * The realm.
	 */
	private String mRealm;


	/**
	 * Private constructor for unparceling.
	 */
	private UserCredentialsSecret()
	{
	}


	/**
	 * Create a secret from the protected secret string.
	 * 
	 * @param protectedSecret
	 *            The obfuscated secret.
	 */
	public UserCredentialsSecret(String protectedSecret)
	{
		super(protectedSecret);
	}


	/**
	 * Create a secret from the user's credentials.
	 * 
	 * @param context
	 *            A Context.
	 * @param username
	 *            The username.
	 * @param password
	 *            The password.
	 * @param realm
	 *            A realm or <code>null</code>.
	 */
	public UserCredentialsSecret(Context context, String username, String password, String realm)
	{
		super(context, username, password, realm);
		mUsername = username;
		mPassword = password;
		mRealm = realm;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.secrets.ProtectedSecret#getScheme()
	 */
	@Override
	public String getScheme()
	{
		return SCHEME;
	}


	/**
	 * Get the username from the secret. Ensure you've called {@link #unprotect(Context)} first if you retrieved this from a parcel or if your created this
	 * instance with {@link #UserCredentialsSecret(String)}.
	 * 
	 * @return The username.
	 */
	public String getUsername()
	{
		return mUsername;
	}


	/**
	 * Get the password from the secret. Ensure you've called {@link #unprotect(Context)} first if you retrieved this from a parcel or if your created this
	 * instance with {@link #UserCredentialsSecret(String)}.
	 * 
	 * @return The password.
	 */
	public String getPassword()
	{
		return mPassword;
	}


	/**
	 * Get the realm from the secret. Ensure you've called {@link #unprotect(Context)} first if you retrieved this from a parcel or if your created this
	 * instance with {@link #UserCredentialsSecret(String)}.
	 * 
	 * @return The realm.
	 */
	public String getRealm()
	{
		return mRealm;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.secrets.ProtectedSecret#parse(java.lang.String)
	 */
	@Override
	protected void parse(String plainSecret)
	{
		String[] parts = split(plainSecret, 3);
		mUsername = parts[0];
		mPassword = parts[1];
		mRealm = parts[2];
	}

	public static final Parcelable.Creator<UserCredentialsSecret> CREATOR = new Parcelable.Creator<UserCredentialsSecret>()
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public UserCredentialsSecret createFromParcel(Parcel in)
		{
			final UserCredentialsSecret state = new UserCredentialsSecret();
			state.readFromParcel(in);
			return state;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public UserCredentialsSecret[] newArray(int size)
		{
			return new UserCredentialsSecret[size];
		}
	};
}
