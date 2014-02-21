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

import java.io.IOException;

import org.dmfs.android.authenticator.secrets.AuthToken;
import org.dmfs.android.authenticator.secrets.StoredSecret;
import org.dmfs.android.authenticator.secrets.UserCredentialsAuthToken;
import org.dmfs.android.authenticator.secrets.UserCredentialsSecret;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.content.Context;
import android.net.Uri;


/**
 * Handler for username + password authentication.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class Password extends AuthSchemeHandler
{
	/**
	 * The scheme this {@link AuthSchemeHandler} handler handles.
	 */
	public final static String SCHEME = "password";

	private final String mLabel;


	/**
	 * Create a new handler for password based authentication. Usually you should use {@link AuthSchemeHandler#get(Context, Uri)} to get an instance of this
	 * class.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param authTokenType
	 *            The auth token type. It must start with <em>password:</em>
	 */
	public Password(Context context, Uri authTokenType)
	{
		super(context, authTokenType);
		if (authTokenType == null || !SCHEME.equals(authTokenType.getScheme()))
		{
			throw new IllegalArgumentException("invalid auth token type for Password authentication: " + authTokenType);
		}
		mLabel = context.getString(R.string.org_dmfs_android_authenticator_authtoken_label_password);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getLabel(android.content.Context)
	 */
	@Override
	public String getLabel(Context context)
	{
		return mLabel;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthToken(android.content.Context, android.accounts.Account)
	 */
	@Override
	protected AuthToken getAuthToken(Context context, Account account)
	{
		UserCredentialsSecret ucs = getSecret(context, account);
		return new UserCredentialsAuthToken(context, ucs.getUsername(), ucs.getPassword(), ucs.getRealm());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthToken(android.content.Context, org.dmfs.android.authenticator.secrets.StoredSecret)
	 */
	@Override
	protected AuthToken getAuthToken(Context context, StoredSecret secret)
	{
		if (!(secret instanceof UserCredentialsSecret))
		{
			throw new IllegalArgumentException("invalid secret type " + secret.getScheme());
		}
		return new UserCredentialsAuthToken(context, ((UserCredentialsSecret) secret).getUsername(), ((UserCredentialsSecret) secret).getPassword(),
			((UserCredentialsSecret) secret).getRealm());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthenticationProvider(android.content.Context, android.accounts.Account)
	 */
	@Override
	public AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, Account account) throws AuthenticatorException, IOException,
		AuthenticationException
	{
		return new PasswordAuthenticationProvider(context, account, mAuthTokenType);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthenticationProvider(android.content.Context,
	 * org.dmfs.android.authenticator.secrets.StoredSecret)
	 */
	@Override
	public AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, StoredSecret secret)
	{
		return new PasswordAuthenticationProvider((UserCredentialsAuthToken) getAuthToken(context, secret), mAuthTokenType);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getSecret(android.content.Context, android.accounts.Account)
	 */
	@Override
	public UserCredentialsSecret getSecret(Context context, Account account)
	{
		UserCredentialsSecret result;
		// de-obfuscate the stored user credentials
		result = new UserCredentialsSecret(getProtectedSecret(context, account));
		result.unprotect(context);

		return result;
	}

}
