package org.dmfs.android.authenticator.test;

import org.dmfs.android.authenticator.obfuscater.Obfuscater;
import org.dmfs.android.authenticator.obfuscater.XOrObfuscater;
import org.dmfs.android.authenticator.secrets.UserCredentialsAuthToken;

import android.content.Context;
import android.test.AndroidTestCase;


public class UserCredentialsAuthTokenTest extends AndroidTestCase
{

	public void userCredentialsAuthTokenTest(Context context, String username, String password, String realm)
	{
		String protectedSecret = new UserCredentialsAuthToken(context, username, password, realm).toString();

		UserCredentialsAuthToken secret = new UserCredentialsAuthToken(protectedSecret);
		secret.unprotect(context);

		assertEquals(username, secret.getUsername());
		assertEquals(password, secret.getPassword());
		assertEquals(realm, secret.getRealm());
	}


	public void testUserCredentialsAuthToken()
	{
		Context context = getContext();
		Obfuscater.INSTANCE.setObfuscaterImpl(new XOrObfuscater());

		userCredentialsAuthTokenTest(context, null, null, null);
		userCredentialsAuthTokenTest(context, "", null, null);
		userCredentialsAuthTokenTest(context, null, "", null);
		userCredentialsAuthTokenTest(context, null, null, "");
		userCredentialsAuthTokenTest(context, "", "", "");
		userCredentialsAuthTokenTest(context, "Test", null, null);
		userCredentialsAuthTokenTest(context, null, "abcdef", null);
		userCredentialsAuthTokenTest(context, null, null, "12345678");
		userCredentialsAuthTokenTest(context, "test", "ABCDEF", null);
		userCredentialsAuthTokenTest(context, "test", "ABCDEF", "12345678");
		userCredentialsAuthTokenTest(context, ",.-;_:!\"§$%&/()=?+#*'", "ABCDEFäöüÖÄÜ", "12345678");
		userCredentialsAuthTokenTest(context, ",.-;_:!\"§$%&/()=?+#*'", "?", "12345678");
		userCredentialsAuthTokenTest(context, ",.-;_:!\"§$%&/()=?+#*'", ",.-;_:!\"§$%&/()=?+#*'", ",.-;_:!\"§$%&/()=?+#*'");
	}

}
