package org.codehaus.xfire.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class PasswordHandler implements CallbackHandler {

	private Map passwords = new HashMap();

	public PasswordHandler() {
		passwords.put("serveralias", "aliaspass");
        passwords.put("client-344-839","client344Password");

	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		String id = pc.getIdentifer();
		pc.setPassword((String) passwords.get(id));

	}

}
