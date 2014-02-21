package org.dmfs.android.authenticator.test;

import org.dmfs.android.authenticator.obfuscater.Obfuscater;
import org.dmfs.android.authenticator.obfuscater.XOrObfuscater;
import org.dmfs.android.authenticator.secrets.UserCredentialsSecret;

import android.content.Context;
import android.test.AndroidTestCase;


public class UserCredentialsSecretTest extends AndroidTestCase
{

	public void userCredentialsSecretTest(Context context, String username, String password, String realm)
	{
		String protectedSecret = new UserCredentialsSecret(context, username, password, realm).toString();

		UserCredentialsSecret secret = new UserCredentialsSecret(protectedSecret);
		secret.unprotect(context);

		assertEquals(username, secret.getUsername());
		assertEquals(password, secret.getPassword());
		assertEquals(realm, secret.getRealm());
	}


	public void testUserCredentialsSecret()
	{
		Context context = getContext();
		Obfuscater.INSTANCE.setObfuscaterImpl(new XOrObfuscater());

		userCredentialsSecretTest(context, null, null, null);
		userCredentialsSecretTest(context, "", null, null);
		userCredentialsSecretTest(context, null, "", null);
		userCredentialsSecretTest(context, null, null, "");
		userCredentialsSecretTest(context, "", "", "");
		userCredentialsSecretTest(context, "Test", null, null);
		userCredentialsSecretTest(context, null, "abcdef", null);
		userCredentialsSecretTest(context, null, null, "12345678");
		userCredentialsSecretTest(context, "test", "ABCDEF", null);
		userCredentialsSecretTest(context, "test", "ABCDEF", "12345678");
		userCredentialsSecretTest(context, ",.-;_:!\"§$%&/()=?+#*'", "ABCDEFäöüÖÄÜ", "12345678");
		userCredentialsSecretTest(context, ",.-;_:!\"§$%&/()=?+#*'", "?", "12345678");
		userCredentialsSecretTest(context, ",.-;_:!\"§$%&/()=?+#*'", ",.-;_:!\"§$%&/()=?+#*'", ",.-;_:!\"§$%&/()=?+#*'");
	}

}
