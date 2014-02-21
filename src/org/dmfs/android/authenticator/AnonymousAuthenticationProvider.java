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

import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.dmfs.android.authenticator.handlers.HttpClientAuthenticationHandler;
import org.dmfs.android.authenticator.secrets.AnonymousAuthToken;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.content.Context;


/**
 * Authentication provider for anonymous authentication.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AnonymousAuthenticationProvider extends AuthenticationProvider<AnonymousAuthToken> implements IHttpClientAuthenticationHandlerFactory
{

	private final static HttpClientAuthenticationHandler HTTP_AUTHENTICATOR = new HttpClientAuthenticationHandler()
	{

		@Override
		public void authenticate(AbstractHttpClient client)
		{
			// nothing to do
		}


		@Override
		public AuthSchemeFactory getPreemptiveAuthSchemeFactory()
		{
			// anonymous authentication doesn't support preemptive authentication
			return null;
		}
	};


	public AnonymousAuthenticationProvider()
	{
		super(null, Anonymous.AUTH_TOKEN_TYPE);
	}


	public AnonymousAuthenticationProvider(Context context, Account account) throws AuthenticatorException, IOException, AuthenticationException
	{
		super(context, account, Anonymous.AUTH_TOKEN_TYPE);
	}


	@Override
	protected void refreshAuthToken(Context context, Account account, AnonymousAuthToken authToken)
	{
		// nothing to do
	}


	@Override
	public boolean canRefresh()
	{
		// we can't refresh the password
		return false;
	}


	@Override
	protected boolean needsRefresh(AnonymousAuthToken authToken)
	{
		return false;
	}


	@Override
	public AnonymousAuthToken getAuthToken(Context context, Account account)
	{
		return new AnonymousAuthToken(context);
	}


	@Override
	public HttpClientAuthenticationHandler getHttpClientAuthenticationHandler(Context context)
	{
		return HTTP_AUTHENTICATOR;
	}

}
