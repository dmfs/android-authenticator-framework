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

import org.dmfs.android.authenticator.handlers.BasicHttpClientAuthenticationHandler;
import org.dmfs.android.authenticator.handlers.HttpClientAuthenticationHandler;
import org.dmfs.android.authenticator.secrets.UserCredentialsAuthToken;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.content.Context;
import android.net.Uri;


/**
 * A class to provide user credentials based authentication.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PasswordAuthenticationProvider extends AuthenticationProvider<UserCredentialsAuthToken> implements IHttpClientAuthenticationHandlerFactory
{

	/**
	 * Create a new authentication provider for password based authentication using the provided auth token.
	 * 
	 * @param authToken
	 *            The authtoken containing the user credentials.
	 * @param authTokenType
	 *            The auth token type (should be a <em>password:</em> Uri).
	 */
	public PasswordAuthenticationProvider(UserCredentialsAuthToken authToken, Uri authTokenType)
	{
		super(authToken, authTokenType);
	}


	/**
	 * Create a new authentication provider for password based authentication for the given account.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account}.
	 * @param authTokenType
	 *            The auth token type (should be a <em>password:</em> Uri).
	 * @throws AuthenticatorException
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public PasswordAuthenticationProvider(Context context, Account account, Uri authTokenType) throws AuthenticatorException, IOException,
		AuthenticationException
	{
		super(context, account, authTokenType);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthenticationProvider#refreshAuthToken(android.content.Context, android.accounts.Account)
	 */
	@Override
	protected void refreshAuthToken(Context context, Account account, UserCredentialsAuthToken authToken)
	{
		// nothing to do
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthenticationProvider#canRefresh()
	 */
	@Override
	public boolean canRefresh()
	{
		// we can't refresh the password
		return false;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthenticationProvider#needsRefresh(T)
	 */
	@Override
	protected boolean needsRefresh(UserCredentialsAuthToken authToken)
	{
		// since we store the password we don't need to refresh it
		return false;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthenticationProvider#getAuthToken(android.content.Context, android.accounts.Account)
	 */
	@Override
	protected UserCredentialsAuthToken getAuthToken(Context context, Account account) throws AuthenticatorException, IOException, AuthenticationException
	{
		String protectedAuthToken = getProtectedAuthToken(context, account);
		UserCredentialsAuthToken authToken = new UserCredentialsAuthToken(protectedAuthToken);
		authToken.unprotect(context);
		return authToken;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.IHttpClientAuthenticationHandlerFactory#getHttpClientAuthenticationHandler(android.content.Context)
	 */
	@Override
	public HttpClientAuthenticationHandler getHttpClientAuthenticationHandler(Context context)
	{
		return new BasicHttpClientAuthenticationHandler(mAuthToken);
	}

}
