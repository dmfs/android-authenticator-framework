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

import org.apache.http.impl.client.AbstractHttpClient;
import org.dmfs.android.authenticator.handlers.AuthenticationHandler;
import org.dmfs.android.authenticator.handlers.HttpClientAuthenticationHandler;

import android.content.Context;


/**
 * A factory to return instances of {@link AuthenticationHandler}s that can authenticate {@link AbstractHttpClient}s using a specific authentication method.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public interface IHttpClientAuthenticationHandlerFactory
{
	/**
	 * Get an {@link AuthenticationHandler} that can authenticate {@link AbstractHttpClient} connections using a specific authentication method.
	 * 
	 * @param context
	 *            A Context.
	 * @return An {@link AuthenticationHandler} instance.
	 */
	public HttpClientAuthenticationHandler getHttpClientAuthenticationHandler(Context context);
}
