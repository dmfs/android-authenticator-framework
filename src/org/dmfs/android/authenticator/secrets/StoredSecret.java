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
 * Subclass of {@link ProtectedSecret} for stored secrets (i.e. what's stored in the account database). This class doesn't do anything more than
 * {@link ProtectedSecret}. It's meant for type safety.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public abstract class StoredSecret extends ProtectedSecret
{
	/**
	 * Hidden constructor for unparceling.
	 */
	StoredSecret()
	{
	}


	/**
	 * Create an instance from the obfuscated secret.
	 * 
	 * @param protectedSecret
	 *            The obfuscated secret.
	 * @see ProtectedSecret#ProtectedSecret(String)
	 */
	public StoredSecret(String protectedSecret)
	{
		super(protectedSecret);
	}


	/**
	 * Create in instance from the secret parts.
	 * 
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param parts
	 *            The parts of the secret.
	 * @see ProtectedSecret#ProtectedSecret(Context, Object...)
	 */
	public StoredSecret(Context context, Object... parts)
	{
		super(context, parts);
	}
}
