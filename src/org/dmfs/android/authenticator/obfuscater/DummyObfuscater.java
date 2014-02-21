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
 * A dummy obfuscater. It doesn't not really obfuscate, instead it just returns all strings in plain text. This is mostly for testing purposes and should not be
 * used in production environments.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class DummyObfuscater extends AbstractObfuscater
{

	/**
	 * Initialize the obfuscater instance.
	 */
	public DummyObfuscater()
	{
	}


	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * <strong>Note:</strong> This method returns <code>plainText</code>. It doesn't obfuscate at all. It's meant for testing purposes.
	 * </p>
	 */
	@Override
	public String obfuscate(Context context, String keyFragment, String plainText)
	{
		return plainText;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.obfuscator.AbstractObfuscater#deobfuscate(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public String deobfuscate(Context context, String keyFragment, String obfuscatedText)
	{
		return obfuscatedText;
	}

}
