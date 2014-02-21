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

package org.dmfs.android.authenticator.secrets;

import android.content.Context;


/**
 * Subclass of {@link ProtectedSecret} for authentication tokens. This class doesn't do anything, it's meant for type safety.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public abstract class AuthToken extends ProtectedSecret
{
	/**
	 * Hidden constructor for unparceling.
	 */
	AuthToken()
	{
	}


	/**
	 * Create an instance from the obfuscated auth token.
	 * 
	 * @param protectedSecret
	 *            The obfuscated auth token.
	 * @see ProtectedSecret#ProtectedSecret(String)
	 */
	public AuthToken(String protectedAuthToken)
	{
		super(protectedAuthToken);
	}


	/**
	 * Create in instance from the auth token parts.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param parts
	 *            The parts of the auth token.
	 * @see ProtectedSecret#ProtectedSecret(Context, Object...)
	 */
	public AuthToken(Context context, Object... parts)
	{
		super(context, parts);
	}
}
