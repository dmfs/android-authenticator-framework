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

import org.dmfs.android.authenticator.R;

import android.content.Context;


/**
 * An obfuscater to protect strings. The actual obfuscater implementation is determined at runtime. The resource String
 * <code>org_dmfs_android_authenticator_obfuscater_provider</code> must provide the class name of an {@link IObfuscaterProvider} that returns the class of the
 * actual obfuscater implementation.
 * <p>
 * <strong>Note:</strong> It's not recommended to use any of the default obfuscater provided with this library. You always should implement your own obfuscater.
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public enum Obfuscater
{
	/**
	 * The actual obfuscater instance.
	 */
	INSTANCE;

	public final static String TAG = "Obfuscater";

	/**
	 * The actual obfuscater implementation.
	 */
	private AbstractObfuscater mObfuscatorImpl;

	/**
	 * An interface of a class with a method to return the obfuscater class.
	 * 
	 * @author Marten Gajda <marten@dmfs.org>
	 */
	public interface IObfuscaterProvider
	{
		/**
		 * Return the obfuscater implementation to be used by the {@link Obfuscater}.
		 * 
		 * @return An {@link AbstractObfuscater} implementation.
		 */
		Class<? extends AbstractObfuscater> getObfuscater();
	}


	/**
	 * Obfuscate the given String. Note, some implementations might not obfuscate <code>null</code> or empty strings and just return the same.
	 * 
	 * @param context
	 *            A Context.
	 * @param keyFragment
	 *            An additional key to use or <code>null</code>. Not all obfuscater implementations use this.
	 * @param plainText
	 *            The plain text to obfuscate.
	 * @return An obfuscated string.
	 * @throws RuntimeException
	 *             if there was an error when instantiating the Obfuscater instance.
	 */
	public String obfuscate(Context context, String keyFragment, String plainText)
	{
		return getObfuscatorImpl(context).obfuscate(context, keyFragment, plainText);
	}


	/**
	 * Return the deobfuscated String.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @param keyFragment
	 *            An additional key to be used when obfuscating the value. Not all implementations use this. In any way you'll have to supply the same value
	 *            that was used when obfuscation the obfuscated value.
	 * @param obfuscatedText
	 *            The obfuscated string.
	 * @return The plain text string.
	 * @throws RuntimeException
	 *             if there was an error when instantiating the Obfuscater instance.
	 */
	public String deobfuscate(Context context, String keyFragment, String obfuscatedText)
	{
		return getObfuscatorImpl(context).deobfuscate(context, keyFragment, obfuscatedText);
	}


	/**
	 * Get the actual obfuscater implementation. Since an obfuscater is stateless, there is no need to synchronize her because it doesn't hurt if two (or more)
	 * instances are created in parallel. Only one of them will survive.
	 * 
	 * @param context
	 *            A {@link Context}.
	 * @return An {@link AbstractObfuscater}.
	 * @throws RuntimeException
	 *             if there was an error when instantiating the Obfuscater instance.
	 */
	private AbstractObfuscater getObfuscatorImpl(Context context)
	{
		if (mObfuscatorImpl == null)
		{
			try
			{
				mObfuscatorImpl = ((IObfuscaterProvider) Class.forName(context.getString(R.string.org_dmfs_android_authenticator_obfuscater_provider))
					.newInstance()).getObfuscater().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new RuntimeException("could not instanicate obfuscater", e);
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException("could not instanicate obfuscater", e);
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException("could not instanicate obfuscater", e);
			}
		}
		return mObfuscatorImpl;
	}


	/**
	 * Set the obfuscater implementation. This is meant for testing purposes. Set the string resource
	 * <code>org_dmfs_android_authenticator_obfuscater_provider</code> to the class name of the {@link IObfuscaterProvider} that provides the obfuscater in
	 * production environments.
	 * 
	 * @param obfuscater
	 *            The obfuscater to use.
	 */
	public void setObfuscaterImpl(AbstractObfuscater obfuscater)
	{
		mObfuscatorImpl = obfuscater;
	}
}
