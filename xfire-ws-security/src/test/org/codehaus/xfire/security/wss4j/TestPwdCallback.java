package org.codehaus.xfire.security.wss4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class TestPwdCallback
    implements CallbackHandler
{

    private static Map passwords = new HashMap();

    static
    {
        passwords.put("myalias", "myAliasPassword");
        passwords.put("myAlias", "myAliasPassword");
    }

    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException
    {
        for (int i = 0; i < callbacks.length; i++)
        {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

            String pass = (String) passwords.get(pc.getIdentifer());
            if (pass != null)
            {
                pc.setPassword(pass);
            }
        }
    }
}
