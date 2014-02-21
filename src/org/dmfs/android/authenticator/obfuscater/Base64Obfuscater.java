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
import android.util.Base64;


/**
 * A very simple obfuscater that encodes the string to Base64. This is just as insecure as plain text.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class Base64Obfuscater extends AbstractObfuscater
{

	/**
	 * Initialize the instance.
	 */
	public Base64Obfuscater()
	{
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>Note:</strong> Base64 encoding is just like plain text, you can decode it easily.
	 * </p>
	 */
	@Override
	public String obfuscate(Context context, String keyFragment, String plainText)
	{
		if (plainText == null)
		{
			return null;
		}
		return Base64.encodeToString(plainText.getBytes(), Base64.NO_WRAP);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.obfuscator.AbstractObfuscater#deobfuscate(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public String deobfuscate(Context context, String keyFragment, String obfuscatedText)
	{
		if (obfuscatedText == null)
		{
			return null;
		}
		return new String(Base64.decode(obfuscatedText, Base64.NO_WRAP));
	}

}
