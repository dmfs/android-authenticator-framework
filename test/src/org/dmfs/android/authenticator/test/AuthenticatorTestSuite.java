package org.dmfs.android.authenticator.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AuthenticatorTestSuite
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTestSuite(UserCredentialsAuthTokenTest.class);
		suite.addTestSuite(UserCredentialsSecretTest.class);
		suite.addTestSuite(ObfuscaterTest.class);
		return suite;
	}

}