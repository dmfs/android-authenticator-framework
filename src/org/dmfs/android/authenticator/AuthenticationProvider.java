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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;


/**
 * An abstract class to provide the actual authentication. This class (and its subclasses) takes care about receiving and de-obfuscating an authtoken from the
 * account manager. Subclasses should implement an interface like {@link IHttpClientAuthenticationHandlerFactory} that returns a handler to authenticate a
 * specific protocol.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 * 
 * @param <T>
 *            The {@link AuthToken} class used by the implementing subclass.
 */
public abstract class AuthenticationProvider<T extends AuthToken>
{
	/**
	 * Maximum number of attempts to retrieve the auth token.
	 */
	private final static int MAX_AUTH_TOKEN_GET_ATTEMPTS = 3;

	/**
	 * The time in milliseconds to wait between two attempts to get the auth token.
	 */
	private final static int MAX_AUTH_TOKEN_ERROR_WAIT_TIME = 200;

	/**
	 * The auth token.
	 */
	protected final T mAuthToken;

	/**
	 * The type of the auth token.
	 */
	protected final Uri mAuthTokenType;


	/**
	 * Initialize the AuthenticationProvider with a specific authToken of a specific type. Subclasses should verify authTokenType to ensure it's the correct
	 * type.
	 * 
	 * @param authToken
	 *            The {@link AuthToken}.
	 * @param authTokenType
	 *            An {@link Uri} containing the auth token type.
	 */
	public AuthenticationProvider(T authToken, Uri authTokenType)
	{
		mAuthTokenType = authTokenType;
		mAuthToken = authToken;
	}


	/**
	 * Initialize the AuthenticationProvider for a specific account. Subclasses should verify authTokenType to ensure it's the correct type. This should not be
	 * called from the main thread, because it might require a network operation to complete.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account}.
	 * @param authTokenType
	 *            The auth token type.
	 * @throws AuthenticatorException
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public AuthenticationProvider(Context context, Account account, Uri authTokenType) throws AuthenticatorException, IOException, AuthenticationException
	{
		mAuthTokenType = authTokenType;
		T authToken = getAuthToken(context, account);
		if (needsRefresh(authToken))
		{
			refreshAuthToken(context, account, authToken);
			authToken = getAuthToken(context, account);
		}
		mAuthToken = authToken;
	}


	/**
	 * Return whether the auth token can be refreshed.
	 * 
	 * @return <code>true</code> if the auth token can be refreshed, <code>false</code> otherwise.
	 */
	public abstract boolean canRefresh();


	/**
	 * Return whether the auth token needs a refresh.
	 * 
	 * @param authToken
	 *            An {@link AuthToken}.
	 * @return <code>true</code> if the token should be refreshed, <code>false</code> if it doesn't need a refresh or it's unknown if it should be refreshed.
	 */
	protected abstract boolean needsRefresh(T authToken);


	/**
	 * Refresh the given auth token.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account}.
	 * @param authToken
	 *            The {@link AuthToken} to refresh.
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	protected void refreshAuthToken(Context context, Account account, T authToken) throws AuthenticatorException, IOException
	{
		AccountManager accountManager = AccountManager.get(context);
		accountManager.invalidateAuthToken(account.type, authToken.toString());
	}


	/**
	 * For a refresh of the auth token.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account}.
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public void refreshAuthToken(Context context, Account account) throws AuthenticatorException, IOException
	{
		refreshAuthToken(context, account, mAuthToken);
	}


	/**
	 * Returns an instance of an {@link AuthToken} for the given account. Don't call it from the main thread since it might cause some network operation to get
	 * the auth token and it blocks until the auth token has been received or an error occurred.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The account.
	 * @return A String containing the obfuscated auth token.
	 * @throws AuthenticatorException
	 * @throws IOException
	 *             if an I/O Error occurred, most likely due to a connection issue.
	 * @throws AuthenticationException
	 *             if no valid auth token was received.
	 */
	protected abstract T getAuthToken(Context context, Account account) throws AuthenticatorException, IOException, AuthenticationException;


	/**
	 * Get the auth token string from the account manager. Don't call it from the main thread since it might cause some network operation to get the auth token
	 * and it blocks until the auth token has been received or an error occurred.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The account.
	 * @return A String containing the obfuscated auth token.
	 * @throws AuthenticatorException
	 * @throws IOException
	 *             if an I/O Error occurred, most likely due to a connection issue.
	 * @throws AuthenticationException
	 *             if no valid auth token was received.
	 */
	protected final String getProtectedAuthToken(Context context, Account account) throws AuthenticatorException, IOException, AuthenticationException
	{
		AccountManager am = AccountManager.get(context);
		int i = MAX_AUTH_TOKEN_GET_ATTEMPTS;
		while (true)
		{
			try
			{
				String authToken = am.blockingGetAuthToken(account, mAuthTokenType.toString(), true /* we handle auth failures ourselves */);
				if (authToken == null && --i == 0)
				{
					throw new AuthenticationException("auth token was null");
				}
				else if (authToken != null)
				{
					return authToken;
				}
			}
			catch (OperationCanceledException e)
			{
				if (--i == 0)
				{
					throw new AuthenticationException("get auth token failed", e);
				}
			}
			catch (AuthenticatorException e)
			{
				if (--i == 0)
				{
					throw e;
				}
			}
			catch (IOException e)
			{
				if (--i == 0)
				{
					throw e;
				}
			}
			// give the authenticator a break
			SystemClock.sleep(MAX_AUTH_TOKEN_ERROR_WAIT_TIME);
		}
	}

}
