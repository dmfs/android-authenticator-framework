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

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.client.AbstractHttpClient;


/**
 * An {@link AuthenticationHandler} for {@link AbstractHttpClient}s. It extends AuthenticationHandler by a method to return an {@link AuthSchemeFactory} that is
 * suitable to authenticate using this scheme preemptively.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public interface HttpClientAuthenticationHandler extends AuthenticationHandler<AbstractHttpClient>
{
	/**
	 * Get an AuthSchemeFactory for {@link AuthScheme}s that can be used to authenticate preemptively.
	 * 
	 * @return An {@link AuthSchemeFactory} or <code>null</code> if this scheme doesn't support preemptive authentication.
	 */
	public AuthSchemeFactory getPreemptiveAuthSchemeFactory();
}
