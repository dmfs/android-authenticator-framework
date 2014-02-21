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

import org.dmfs.android.authenticator.secrets.AnonymousAuthToken;
import org.dmfs.android.authenticator.secrets.AnonymousSecret;
import org.dmfs.android.authenticator.secrets.AuthToken;
import org.dmfs.android.authenticator.secrets.StoredSecret;

import android.accounts.Account;
import android.content.Context;
import android.net.Uri;


/**
 * Handler for anonymous authentication.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class Anonymous extends AuthSchemeHandler
{
	/**
	 * The scheme this {@link AuthSchemeHandler} handler handles.
	 */
	public final static String SCHEME = "anonymous";

	/**
	 * The auth token type this class support.
	 */
	public final static Uri AUTH_TOKEN_TYPE = Uri.parse(SCHEME + ":");

	/**
	 * A static instance of the {@link AuthenticationProvider} this class returns.
	 */
	private final static AnonymousAuthenticationProvider AUTHENTICATION_PROVIDER = new AnonymousAuthenticationProvider();

	/**
	 * The cached label.
	 */
	private final String mLabel;


	public Anonymous(Context context, Uri authTokenType)
	{
		super(context, AUTH_TOKEN_TYPE);
		if (authTokenType == null || !SCHEME.equals(authTokenType.getScheme()))
		{
			throw new IllegalArgumentException("invalid auth token type for anonymous authentication: " + authTokenType);
		}
		mLabel = context.getString(R.string.org_dmfs_android_authenticator_authtoken_label_anonymous);
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
		return new AnonymousAuthToken(context);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthToken(android.content.Context, org.dmfs.android.authenticator.secrets.StoredSecret)
	 */
	@Override
	protected AuthToken getAuthToken(Context context, StoredSecret secret)
	{
		return new AnonymousAuthToken(context);
	}


	/*
	 * Returns a static AnonymousAuthenticationProvider instance.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthenticationProvider(android.content.Context, android.accounts.Account)
	 */
	@Override
	public AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, Account account)
	{
		return AUTHENTICATION_PROVIDER;
	}


	/*
	 * Returns a static AnonymousAuthenticationProvider instance.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getAuthenticationProvider(android.content.Context,
	 * org.dmfs.android.authenticator.secrets.StoredSecret)
	 */
	@Override
	public AuthenticationProvider<? extends AuthToken> getAuthenticationProvider(Context context, StoredSecret secret)
	{
		return AUTHENTICATION_PROVIDER;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.AuthSchemeHandler#getSecret(android.content.Context, android.accounts.Account)
	 */
	@Override
	public StoredSecret getSecret(Context context, Account account)
	{
		// just return a new secret
		return new AnonymousSecret(context);
	}
}
