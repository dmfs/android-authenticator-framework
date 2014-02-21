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
 * A very simple obfuscater that uses the XOr operation to modify the plain text and Base64 to encode the result. It slightly more "secure" than the simple
 * {@link Base64Obfuscater}, but you can still attack is easily, since there is no randomness in it.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class XOrObfuscater extends AbstractObfuscater
{

	/**
	 * A key that's used to Xor the value. You probably want to change this one, to make it slightly more difficult to deobfuscate your secrets.
	 */
	private final static byte[] KEY1 = "uh9goBJKb97geüonbKJbb7hajds".getBytes();

	/**
	 * Another key. You probably want to change this one, to make it slightly more difficult to deobfuscate your secrets.
	 */
	private final static byte[] KEY2 = { -49, -113, 60, -45, 0, -24, 83, -58, -11, -91, -69, -36, -85, 34, 24, 53, 20, -44, 33, -81, 60, -23, 121, 55, -81,
		-48, 70, -60, -7, 68, 28, -87, 53, -102, -77, -68, -41, 9, -31, 0, -30, 98, 0, -38, -119, 115, -115, 68, -102, -108, -20, -75, -92, -108, -99, -70, 12,
		-35, -38, -12, 55, 60, -92, 82, 25, 24, 80, -30, 22, 126, -113, -35, 7, -90, 33, -99, 47, -97, 32, -112, -12, 64, -30, -90, 53, 115, 49, -43, 3, 0, 33,
		-64, -79, -113, 103, -43, -77, -35, -10, -15 };


	/**
	 * Initialize the instance.
	 */
	public XOrObfuscater()
	{
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.obfuscater.AbstractObfuscater#obfuscate(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public String obfuscate(Context context, String keyFragment, String plainText)
	{
		if (plainText == null || plainText.length() == 0)
		{
			return plainText;
		}

		byte[] xored = xor(plainText.getBytes(), xor(KEY1, KEY2));
		if (keyFragment != null && keyFragment.length() > 0)
		{
			xored = xor(xored, keyFragment.getBytes());
		}

		return Base64.encodeToString(xored, Base64.NO_WRAP);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.obfuscator.AbstractObfuscater#deobfuscate(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public String deobfuscate(Context context, String keyFragment, String obfuscatedText)
	{
		if (obfuscatedText == null || obfuscatedText.length() == 0)
		{
			return obfuscatedText;
		}

		byte[] xored = Base64.decode(obfuscatedText, Base64.NO_WRAP);

		if (keyFragment != null && keyFragment.length() > 0)
		{
			xored = xor(xored, keyFragment.getBytes());
		}

		return new String(xor(xored, xor(KEY1, KEY2)));
	}


	/**
	 * XOR's two byte arrays. The result will have the lengths of the first array. If the second array is shorter than the first one it's just wrapped around
	 * and reused.
	 * 
	 * @param first
	 *            An array of bytes.
	 * @param second
	 *            Another array of bytes.
	 */
	private static byte[] xor(byte[] first, byte[] second)
	{
		byte[] result = new byte[first.length];
		final int secondLength = second.length;
		for (int i = 0, len = first.length; i < len; i++)
		{
			result[i] = (byte) (first[i] ^ second[i % secondLength]);
		}
		return result;
	}

}
