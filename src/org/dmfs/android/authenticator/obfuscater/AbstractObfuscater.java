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

package org.dmfs.android.authenticator.obfuscater;

import android.content.Context;


/**
 * An abstract obfuscater for strings.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public abstract class AbstractObfuscater
{
	/**
	 * Obfuscate the given plain text.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param keyFragment
	 *            A key fragment to use or <code>null</code>. Some implementations may support a custom key. In that case you can provide a fragment of the key
	 *            to increase the protection.
	 * @param plainText
	 *            The plain text to obfuscate.
	 * @return The obfuscated text.
	 */
	public abstract String obfuscate(Context context, String keyFragment, String plainText);


	/**
	 * De-obfuscate the given obfuscated text.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param keyFragment
	 *            A key fragment to use or <code>null</code>. This must be the same value that was provided to {@link #obfuscate(Context, String, String)} when
	 *            the string was obfuscated.
	 * @param obfuscatedText
	 *            The plain text to de-obfuscate.
	 * @return The plain text.
	 */
	public abstract String deobfuscate(Context context, String keyFragment, String obfuscatedText);
}
