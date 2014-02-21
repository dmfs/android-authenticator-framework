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

import java.util.regex.Pattern;

import org.dmfs.android.authenticator.obfuscater.Obfuscater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Parcelable class to transport and store secrets in an obfuscated form. Using a ProtectedSecret you can pass secret values around in a {@link Bundle} or store
 * is as a {@link String}, making it hard for other apps to get the actual values.
 * 
 * It maintains the secret in the following scheme:
 * 
 * <pre>
 * &lt;schema>:&lt;obfuscated secret>
 * </pre>
 * 
 * The obfuscated secret may consist of several parts that are joined and stored in an obfuscated form. The individual parts are padded with random strings to
 * make deobfuscation more difficult.
 * 
 * <p>
 * A concrete subclass must have a <code>public static</code> member <code>CREATOR</code> like:
 * </p>
 * 
 * <pre>
 * 	public static final Parcelable.Creator&lt;T> CREATOR = new Parcelable.Creator&lt;T>()
 *  	{
 *      	...
 *  	}
 * </pre>
 * 
 * where <code>T</code> is the name of the subclass.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
@SuppressLint("ParcelCreator")
public abstract class ProtectedSecret implements Parcelable
{
	/**
	 * The characters to pick from when padding with random strings. Those are all values that can appear in encoded parts.
	 */
	private final static char[] RANDOM_CHAR_POOL = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
		'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
		'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_', '-', '!', '.', '~', '\'', '(', ')', '*', '"', '%' };

	/**
	 * Max number of random chars to add before and after a part.
	 */
	private final static int MAX_RANDOM_PADDING = 16;

	/**
	 * The delimiter we use to separate the parts of the secret.
	 */
	@Deprecated
	private final static String DELIMITER = ":";

	/**
	 * The delimiter we use to separate the scheme.
	 */
	private final static char SCHEME_DELIMITER = ':';

	/**
	 * The string that is stored for <code>null</code> values. Since "?" will be encoded as "%3F" in non-null values this won't cause any collisions.
	 */
	private final static String NULL_VALUE = "?";

	/**
	 * Characters that delimit random character sequences that have been added. All these characters would be encoded by {@link Uri#encode(String)}, so they
	 * don't conflict with characters in the secret.
	 */
	private final static char[] RANDOM_DELIMITERS = { '+', '/', '$', '&', '=', '#', '@' };

	/**
	 * Pattern that matches any of the characters in {@link #RANDOM_DELIMITERS}.
	 */
	private final static Pattern RANDOM_DELIMITER_PATTERN = Pattern.compile("[" + new String(RANDOM_DELIMITERS) + "]");

	/**
	 * The obfuscated secret. We can't make it final to support unparcelling.
	 */
	private String mProtectedSecret;


	/**
	 * Hidden constructor for unparceling purposes.
	 */
	ProtectedSecret()
	{
	}


	/**
	 * Create instance from the given obfuscated secret.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param protectedSecret
	 *            The obfuscated secret.
	 */
	public ProtectedSecret(String protectedSecret)
	{
		String scheme = getScheme();
		if (protectedSecret == null)
		{
			throw new IllegalArgumentException("invalid protected secret, expected scheme " + scheme + ", but secret was null");
		}
		if (protectedSecret.length() <= scheme.length() || !protectedSecret.startsWith(scheme) || protectedSecret.charAt(scheme.length()) != SCHEME_DELIMITER)
		{
			throw new IllegalArgumentException("invalid protected secret, expected scheme " + scheme + ", but secret started with "
				+ protectedSecret.substring(0, Math.min(protectedSecret.length(), 10)));
		}
		mProtectedSecret = protectedSecret;
	}


	/**
	 * Create a protected secret from the given parts. The secret will be serialized as concatenation of the scheme and the obfuscated parts in the given order.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param parts
	 *            The parts of the secret.
	 */
	public ProtectedSecret(Context context, Object... parts)
	{
		mProtectedSecret = getScheme() + SCHEME_DELIMITER + Obfuscater.INSTANCE.obfuscate(context, null, join(parts));
	}


	/**
	 * De-obfuscate the stored secret. You'll have to call this when the instance has been created from the obfuscated secret or when it has been unparceled
	 * (e.g. when you got it from a {@link Bundle}).
	 * 
	 * @param context
	 *            A {@link Context}.
	 */
	public void unprotect(Context context)
	{
		String scheme = getScheme();
		parse(Obfuscater.INSTANCE.deobfuscate(context, null, mProtectedSecret.substring(scheme.length() + 1)));
	}


	/**
	 * Split the plain secret into its parts.
	 * 
	 * @param plainSecret
	 *            The plan secret.
	 * @param parts
	 *            The number of parts the actual secret consists of.
	 * @return An array of strings with the parts of the secret like they have been stored.
	 */
	protected static String[] split(String plainSecret, int parts)
	{
		if (plainSecret == null)
		{
			throw new IllegalArgumentException("secret must not be null");
		}

		String[] splitParts = RANDOM_DELIMITER_PATTERN.split(plainSecret, parts * 2 + 1);

		if (splitParts == null || splitParts.length != parts * 2 + 1)
		{
			// might be a secret stored with the old scheme
			// TODO: remove splitOld and throw an exception instead
			return splitOld(plainSecret, parts);
		}

		String[] result = new String[splitParts.length / 2];

		// decode all parts
		for (int i = 0; i < parts; i++)
		{
			String value = splitParts[i * 2 + 1];

			if (NULL_VALUE.equals(value))
			{
				result[i] = null;
			}
			else
			{
				result[i] = Uri.decode(value);
			}
		}

		return result;
	}


	/**
	 * Split the plain secret into its parts.
	 * <p>
	 * This method is here for backwards compatibility. It will be removed in future releases.
	 * </p>
	 * 
	 * @param plainSecret
	 *            The plan secret.
	 * @param parts
	 *            The number of parts the actual secret consists of.
	 * @return An array of strings with the parts of the secret like they have been stored.
	 */
	@Deprecated
	protected static String[] splitOld(String plainSecret, int parts)
	{
		if (plainSecret == null)
		{
			throw new IllegalArgumentException("secret must not be null");
		}

		String[] result = plainSecret.split(DELIMITER, parts);

		if (result == null || result.length != parts)
		{
			throw new IllegalArgumentException("invalid secret, number of parts doesn't match");
		}

		// decode all parts
		for (int i = 0; i < parts; i++)
		{
			if (NULL_VALUE.equals(result[i]))
			{
				result[i] = null;
			}
			else
			{
				result[i] = Uri.decode(result[i]);
			}
		}

		return result;
	}


	/**
	 * Join the parts of the secret to a {@link String}.
	 * 
	 * @param parts
	 *            The parts of the secret.
	 * @return The concatenation of the parts, separated by {@link #DELIMITER}.
	 */
	private static String join(Object... parts)
	{
		StringBuilder result = new StringBuilder(1024);
		for (Object part : parts)
		{
			appendRandomString(result, RANDOM_CHAR_POOL, 0, MAX_RANDOM_PADDING);
			appendRandomString(result, RANDOM_DELIMITERS, 1, 1);
			result.append(part == null ? NULL_VALUE : Uri.encode(part.toString()));
			appendRandomString(result, RANDOM_DELIMITERS, 1, 1);
			appendRandomString(result, RANDOM_CHAR_POOL, 0, MAX_RANDOM_PADDING);
		}
		return result.toString();
	}


	/**
	 * Append a random String to a {@link StringBuilder}.
	 * 
	 * @param stringBuilder
	 *            The {@link StringBuilder} to write to.
	 * @param charPool
	 *            The pool to chose the random characters from.
	 * @param minLength
	 *            The min number of characters to write.
	 * @param maxLength
	 *            The max number of characters to append.
	 */
	private static void appendRandomString(StringBuilder stringBuilder, char[] charPool, int minLength, int maxLength)
	{
		int len = minLength == maxLength ? minLength : Math.max(minLength, (int) (Math.random() * maxLength));
		int poolLen = charPool.length;
		while (--len >= 0)
		{
			stringBuilder.append(charPool[(int) (Math.random() * poolLen)]);
		}
	}


	/**
	 * Parse the given plain secret into its parts.
	 * 
	 * Use {@link #split(String, String, int)} to split the plain secret into parts and get the parts from the resulting array.
	 * 
	 * @param plainSecret
	 *            The joined parts.
	 */
	protected abstract void parse(String plainSecret);


	public abstract String getScheme();


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return mProtectedSecret;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents()
	{
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(mProtectedSecret);
	}


	/**
	 * Read the protected secret from the given {@link Parcel}.
	 * 
	 * @param source
	 *            The {@link Parcel} to read from.
	 */
	protected void readFromParcel(Parcel source)
	{
		mProtectedSecret = source.readString();
	}
}
