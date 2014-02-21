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

package org.dmfs.android.authenticator.handlers;

import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.dmfs.android.authenticator.secrets.UserCredentialsAuthToken;


/**
 * A handler that prepares an {@link AbstractHttpClient} for password authentication. At present this class assumes that the AbstractHttpClient has registered
 * suitable authentication schemes (i.e. for Basic and/or Digest authentication).
 * <p>
 * TODO: allow to limit authentication schemes (i.e. disallow Basic authentication).
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class BasicHttpClientAuthenticationHandler implements HttpClientAuthenticationHandler
{
	/**
	 * A factory for the {@link BasicScheme}.
	 */
	private final static AuthSchemeFactory BASIC_SCHEME_FACTORY = new BasicSchemeFactory();

	/**
	 * The auth token that provides username and password.
	 */
	private UserCredentialsAuthToken mAuthToken;


	/**
	 * Construct a new handler for basic Http authentication.
	 * 
	 * @param authToken
	 *            The authoken to use for authentication.
	 */
	public BasicHttpClientAuthenticationHandler(UserCredentialsAuthToken authToken)
	{
		mAuthToken = authToken;
	}


	@Override
	public void authenticate(AbstractHttpClient client)
	{
		// just set the credentials assuming that proper authentication schemes are registered with the AbstractHttpClient.
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, mAuthToken.getRealm()),
			new UsernamePasswordCredentials(mAuthToken.getUsername(), mAuthToken.getPassword()));

		client.setCredentialsProvider(credsProvider);
	}


	@Override
	public AuthSchemeFactory getPreemptiveAuthSchemeFactory()
	{
		return BASIC_SCHEME_FACTORY;
	}
}
