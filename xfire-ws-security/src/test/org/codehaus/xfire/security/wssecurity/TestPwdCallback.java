package org.codehaus.xfire.security.wssecurity;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.security.CallbackInfo;
import org.codehaus.xfire.security.WSPasswordCallback;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class TestPwdCallback
    implements WSPasswordCallback
{

    private static Map passwords = new HashMap();

    static
    {
        passwords.put("alias", "aliaspass");
        passwords.put("userName", "userSecretePass");

    }

    public String handle(CallbackInfo info)
    {
        return (String) passwords.get(info.getId());

    }

}
