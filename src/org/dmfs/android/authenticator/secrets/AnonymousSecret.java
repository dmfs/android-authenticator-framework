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
import android.os.Parcel;
import android.os.Parcelable;


/**
 * The secret for anonymous authentication. It doesn't store anything at all (beside the scheme), but it's handy to have.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AnonymousSecret extends StoredSecret
{

	private final static String SCHEME = "anonymous_secret";


	/**
	 * Private constructor for unparceling.
	 */
	private AnonymousSecret()
	{
	}


	/**
	 * Create anonymous secret from the protected secret.
	 * 
	 * @param context
	 *            A {@link Context}.
	 */
	public AnonymousSecret(String protectedSecret)
	{
		super(protectedSecret);
	}


	/**
	 * Create anonymous secret.
	 * 
	 * @param context
	 *            A {@link Context}.
	 */
	public AnonymousSecret(Context context)
	{
		super(context);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.secrets.ProtectedSecret#getScheme()
	 */
	@Override
	public String getScheme()
	{
		return SCHEME;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dmfs.android.authenticator.secrets.ProtectedSecret#parse(java.lang.String)
	 */
	@Override
	protected void parse(String plainSecret)
	{
	}

	public static final Parcelable.Creator<AnonymousSecret> CREATOR = new Parcelable.Creator<AnonymousSecret>()
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public AnonymousSecret createFromParcel(Parcel in)
		{
			final AnonymousSecret state = new AnonymousSecret();
			state.readFromParcel(in);
			return state;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public AnonymousSecret[] newArray(int size)
		{
			return new AnonymousSecret[size];
		}
	};
}
