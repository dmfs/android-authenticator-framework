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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.dmfs.android.authenticator.secrets.AuthToken;
import org.dmfs.android.authenticator.secrets.StoredSecret;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;


/**
 * An abstract handler for authentication schemes. Subclass this to provide a handler for a specific authentication protocol and use {@link #get(Context, Uri)}
 * to get an instance for a specific authTokenType.
 * 
 * In your project define the list of supported authentication schemes like
 * 
 * <pre>
 *     &lt;!-- Supported authenticator schemes. Add one item for each scheme here and add the handler class at the same index in org_dmfs_android_authenticator_auth_scheme_handlers below. -->
 *     &lt;string-array name="org_dmfs_android_authenticator_auth_schemes">
 *         &lt;item>password&lt;/item>
 *         &lt;item>anonymous&lt;/item>
 *     &lt;/string-array>
 * 
 *     &lt;!-- Fully qualified names of classes that handle the auth scheme at the same index in org_dmfs_android_authenticator_auth_schemes. -->
 *     &lt;string-array name="org_dmfs_android_authenticator_auth_scheme_handlers">
 *         &lt;item>org.dmfs.android.authenticator.Password&lt;/item>
 *         &lt;item>org.dmfs.android.authenticator.Anonymous&lt;/item>
 *     &lt;/string-array>
 * </pre>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public abstract class AuthSchemeHandler
{
	/**
	 * An array of auth schemes. This will be initialized on the first call to {@link #get(Context, String)}.
	 */
	private static String[] AUTH_SCHEMES;

	/**
	 * An array of handler class names for the auth schemes in {@link #AUTH_SCHEMES}. This will be initialized on the first call to
	 * {@link #get(Context, String)}.
	 */
	private static String[] AUTH_SCHEME_CLASSES;

	/**
	 * The auth token type this handler was created for.
	 */
	protected final Uri mAuthTokenType;


	/**
	 * Construct a new {@link AuthSchemeHandler} for the given {@link AuthSchemeHandler} token type. Subclasses should either protect their constructors or
	 * check that the scheme of the auth token type is valid.
	 * 
	 * @param context
	 * @param authTokenType
	 */
	protected AuthSchemeHandler(Context context, Uri authTokenType)
	{
		mAuthTokenType = authTokenType;
	}


	/**
	 * Returns an {@link AuthSchemeHandler} for a specific authentication scheme.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param authTokenType
	 *            The auth token type uri.
	 * @return A specific {@link AuthSchemeHandler} instance or <code>null</code> if no handler has been specified for this auth token type.
	 */
	public static AuthSchemeHandler get(Context context, String authTokenType)
	{
		return get(context, Uri.parse(authTokenType));
	}


	/**
	 * Returns an {@link AuthSchemeHandler} for a specific authentication scheme.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param authTokenType
	 *            The auth token type Uri.
	 * @return A specific {@link AuthSchemeHandler} instance or <code>null</code> if no handler has been specified for this auth token type.
	 */
	public static AuthSchemeHandler get(Context context, Uri authTokenType)
	{
		Resources res = context.getResources();
		if (AUTH_SCHEMES == null)
		{
			// cache arrays, there is no need to synchronize since the expected result is always the same
			AUTH_SCHEMES = res.getStringArray(R.array.org_dmfs_android_authenticator_auth_schemes);
			AUTH_SCHEME_CLASSES = res.getStringArray(R.array.org_dmfs_android_authenticator_auth_scheme_handlers);
		}

		for (int i = 0, count = AUTH_SCHEMES.length; i < count; ++i)
		{
			if (AUTH_SCHEMES[i].equals(authTokenType.getScheme()))
			{
				String className = AUTH_SCHEME_CLASSES[i];
				try
				{
					Constructor<?> constructor = Class.forName(className).getConstructor(Context.class, Uri.class);
					AuthSchemeHandler result = (AuthSchemeHandler) constructor.newInstance(context, authTokenType);

					return result;
				}
				catch (InstantiationException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
				catch (ClassNotFoundException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
				catch (NoSuchMethodException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
				catch (IllegalArgumentException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
				catch (InvocationTargetException e)
				{
					throw new RuntimeException("can't instanciate " + className, e);
				}
			}
		}
		return null;
	}


	/**
	 * Return a label for this authentication scheme.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @return A label for this auth scheme.
	 */
	public abstract String getLabel(Context context);


	/**
	 * Get an authentication token for the specified account and protected secret string.
	 * <p>
	 * <strong>Note:</strong> Some {@link AuthSchemeHandler}s will have to make a network connection to get the auth token, so don't call this method from the
	 * main thread!
	 * </p>
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account} for which to get the auth token.
	 * @return An {@link AuthToken}.
	 * @throws AuthenticationException
	 * @throws IOException
	 */
	protected abstract AuthToken getAuthToken(Context context, Account account) throws AuthenticationException, IOException;


	/**
	 * Get an authentication token for the specified account and protected secret string.
	 * <p>
	 * <strong>Note:</strong> Some {@link AuthSchemeHandler}s will have to make a network connection to get the auth token, so don't call this method from the
	 * main thread!
	 * </p>
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param secret
	 *            The secret stored in the account database.
	 * @return An {@link AuthToken}.
	 * @throws AuthenticationException
	 * @throws IOException
	 */
	protected abstract AuthToken getAuthToken(Context context, StoredSecret secret) throws AuthenticationException, IOException;


	/**
	 * Return an {@link AuthenticationProvider} for a specific {@link Account} that knows how to authenticate.
	 * <p>
	 * <strong>Note:</strong> For some auth schemes this method will have to make a network connection to get an authToken of none is stored yet or it has
	 * expired, so don't call it from the main thread!
	 * </p>
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account}.
	 * @return An {@link AuthenticationProvider} instance.
	 * @throws UnauthorizedException
	 * @throws IOException
	 * @throws AuthenticatorException
	 * @throws AuthenticationException
	 */
	public abstract AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, Account account) throws AuthenticatorException,
		IOException, AuthenticationException;


	/**
	 * Return an {@link AuthenticationProvider} for a specific {@link Account} that knows how to authenticate.
	 * <p>
	 * <strong>Note:</strong> For some auth schemes this method will have to make a network connection to get an authToken for the given {@link StoredSecret},
	 * so don't call it from the main thread!
	 * </p>
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param storedSecret
	 *            A {@link StoredSecret} to use for authentication.
	 * 
	 * @return An {@link AuthenticationProvider} instance.
	 * @throws AuthenticatorException
	 * @throws IOException
	 * @throws UnauthorizedException
	 * @throws AuthenticationException
	 */
	public abstract AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, StoredSecret secret) throws AuthenticatorException,
		IOException, AuthenticationException;


	/**
	 * Return the {@link StoredSecret} instance for the given account. The returned {@link StoredSecret} is already unprotected, so there is no need to call the
	 * <code>unprotect</code> method again.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account} for which to get the {@link StoredSecret}.
	 * @return An instance of a subclass of {@link StoredSecret}.
	 * 
	 * @throws IllegalArgumentException
	 *             if the type of the stored secret is not supported by this handler.
	 */
	public abstract StoredSecret getSecret(Context context, Account account);


	/**
	 * Return the secret string that's stored in the account database.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param account
	 *            The {@link Account} for which to get the password.
	 * @return A String that contains the secret String for the given account.
	 */
	protected String getProtectedSecret(Context context, Account account)
	{
		AccountManager am = AccountManager.get(context);
		return am.getPassword(account);
	}
}
