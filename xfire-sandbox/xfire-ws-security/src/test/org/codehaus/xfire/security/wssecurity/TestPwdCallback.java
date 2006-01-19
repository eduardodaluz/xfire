package org.codehaus.xfire.security.wssecurity;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.security.WSPasswordCallback;

public class TestPwdCallback
    implements WSPasswordCallback
{

    private static Map passwords = new HashMap();

    static
    {
        passwords.put("alias", "aliaspass");
        passwords.put("userName", "userSecretePass");

    }

    public String handle(String id, boolean hashed, int usg)
    {
        return (String) passwords.get(id);

    }

}
