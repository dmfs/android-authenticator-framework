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
 * The secret for anonymous authentication. It doesn't take or provide any data, but it's handy to have.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AnonymousAuthToken extends AuthToken
{

	private final static String SCHEME = "anonymous_auth_token";


	/**
	 * Private constructor for unparceling.
	 */
	private AnonymousAuthToken()
	{
	}


	/**
	 * Create a new auth token for anonymous authentication.
	 * 
	 * @param protectedAuthToken
	 */
	public AnonymousAuthToken(String protectedAuthToken)
	{
		super(protectedAuthToken);
	}


	/**
	 * Create a new auth token for anonymous authentication.
	 * 
	 * @param protectedAuthToken
	 */
	public AnonymousAuthToken(Context context)
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

	public static final Parcelable.Creator<AnonymousAuthToken> CREATOR = new Parcelable.Creator<AnonymousAuthToken>()
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public AnonymousAuthToken createFromParcel(Parcel in)
		{
			final AnonymousAuthToken state = new AnonymousAuthToken();
			state.readFromParcel(in);
			return state;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public AnonymousAuthToken[] newArray(int size)
		{
			return new AnonymousAuthToken[size];
		}
	};
}
