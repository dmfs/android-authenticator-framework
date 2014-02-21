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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/**
 * A modular authenticator. It acts mostly as a switch. The actual authentication is handled by {@link AuthSchemeHandler} instances.
 * <p>
 * TODO: populate all methods and return some reasonable results instead of just <code>null</code>.
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class Authenticator extends AbstractAccountAuthenticator
{
	private final static String TAG = "org.dmfs.android.authenticator.Authenticator";

	/**
	 * The action that is called to create a new account.
	 */
	public final static String ACTION_ADD_ACCOUNT = "org.dmfs.android.authenticator.action.ADD_ACCOUNT";

	/**
	 * The application context we're living in.
	 */
	private final Context mContext;


	/**
	 * Build a new Authenticator instance.
	 * 
	 * @param context
	 *            A {@link Context}.
	 */
	public Authenticator(Context context)
	{
		super(context);
		mContext = context.getApplicationContext();
	}


	/*
	 * This basically returns an intent for the action ACTION_ADD_ACCOUNT. The package should have at least one Activity that listens for this action without
	 * any filters.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts.AccountAuthenticatorResponse, java.lang.String, java.lang.String,
	 * java.lang.String[], android.os.Bundle)
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
		throws NetworkErrorException
	{
		final Intent intent = new Intent(ACTION_ADD_ACCOUNT);

		intent.setPackage(mContext.getPackageName());
		if (authTokenType != null)
		{
			intent.setData(Uri.parse(authTokenType));
		}
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}


	/*
	 * This is called to check that a user knows the credentials of an account. This should return an Intent that prompts the user to authenticate.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * android.os.Bundle)
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException
	{
		// we don't support that yet
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#editProperties(android.accounts.AccountAuthenticatorResponse, java.lang.String)
	 */
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
	{
		throw new UnsupportedOperationException();
	}


	/*
	 * Get the AuthSchemeHandler for the given account and use it to get an auth token from the stored secret.
	 * 
	 * TODO: maybe return an Intent for an authentication prompt instead if getting an auth token failed.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String, android.os.Bundle)
	 */
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
	{
		AuthSchemeHandler authSchemeHandler = AuthSchemeHandler.get(mContext, authTokenType);

		if (authSchemeHandler == null)
		{
			// unknown auth token type
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "unknown auth token type");
			return result;
		}

		final AccountManager am = AccountManager.get(mContext);
		String secret = am.getPassword(account);
		if (secret != null)
		{
			// we do have a secret
			try
			{
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				result.putString(AccountManager.KEY_AUTHTOKEN, authSchemeHandler.getAuthToken(mContext, account).toString());
				return result;
			}
			catch (AuthenticationException e)
			{
				Log.e(TAG, "could not retrieve auth token", e);

				final Bundle result = new Bundle();

				// result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_AUTHENTICATION);
				result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());

				// this will return a null auth token to the caller (if the notifyAuthFailure parameter was true when calling blockingGetAuthToken)
				return result;
			}
			catch (IllegalArgumentException e)
			{
				Log.e(TAG, "could not retrieve auth token", e);
				// the secret was of an unexpected format
				final Bundle result = new Bundle();

				result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());

				// this will return a null auth token to the caller (if the notifyAuthFailure parameter was true when calling blockingGetAuthToken)
				return result;
			}
			catch (IOException e)
			{
				Log.e(TAG, "could not retrieve auth token", e);
				// this should return an OperationCancelledException to the caller
				throw new NetworkErrorException("could not get auth token", e);
			}
		}
		else
		{
			final Bundle result = new Bundle();

			result.putString(AccountManager.KEY_ERROR_MESSAGE, "no stored secret found");

			// this will return a null authentication token to the caller
			return result;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.lang.String)
	 */
	@Override
	public String getAuthTokenLabel(String authTokenType)
	{
		AuthSchemeHandler handler = AuthSchemeHandler.get(mContext, authTokenType);

		if (handler == null)
		{
			// unknown auth token type
			if (Log.isLoggable(TAG, Log.ERROR))
			{
				Log.e(TAG, "unknown auth token type: '" + authTokenType + "'");
			}
			return null;
		}

		return handler.getLabel(mContext);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String[])
	 */
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException
	{
		// for now we return false for every feature
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.accounts.AbstractAccountAuthenticator#updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account,
	 * java.lang.String, android.os.Bundle)
	 */
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
	{
		// currently not supported
		return null;
	}

}
