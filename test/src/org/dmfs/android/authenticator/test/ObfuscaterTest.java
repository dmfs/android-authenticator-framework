package org.dmfs.android.authenticator.test;

import org.dmfs.android.authenticator.obfuscater.AbstractObfuscater;
import org.dmfs.android.authenticator.obfuscater.Base64Obfuscater;
import org.dmfs.android.authenticator.obfuscater.DummyObfuscater;
import org.dmfs.android.authenticator.obfuscater.XOrObfuscater;

import android.content.Context;
import android.test.AndroidTestCase;


public class ObfuscaterTest extends AndroidTestCase
{
	private final static String[] TEST_STRINGS = { null, "", " ", ";:_,.-=!\"§$%&/()=?*\'", "8hd87gfabzugdv",
		"A string that needs to be obfuscated to ensure no one can read it easily." };


	/**
	 * Test the given obfuscater with all String as {@link #TEST_STRINGS} as key and plain text.
	 * 
	 * @param obfuscater
	 *            The {@link AbstractObfuscater} to test.
	 */
	public void obfuscaterTest(AbstractObfuscater obfuscater)
	{
		Context context = getContext();

		for (String key : TEST_STRINGS)
		{
			for (String plainText : TEST_STRINGS)
			{
				// ensure obfuscating and deobfuscating gives the original plain text, that's all we want
				assertEquals(plainText, obfuscater.deobfuscate(context, key, obfuscater.obfuscate(context, key, plainText)));
			}
		}
	}


	/**
	 * Tests all {@link AbstractObfuscater} implementations in this library.
	 */
	public void testAllObfuscaters()
	{
		obfuscaterTest(new XOrObfuscater());
		obfuscaterTest(new DummyObfuscater());
		obfuscaterTest(new Base64Obfuscater());
	}
}
