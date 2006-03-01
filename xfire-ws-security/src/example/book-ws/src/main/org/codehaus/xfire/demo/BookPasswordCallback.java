package org.codehaus.xfire.demo;

import org.codehaus.xfire.security.CallbackInfo;
import org.codehaus.xfire.security.WSPasswordCallback;

/**
 * @author tomeks
 *
 */
public class BookPasswordCallback
    implements WSPasswordCallback
{

    public String handle(CallbackInfo info)
    {
        if (info.getId().equals("tomek"))
        {
            return "secretPass";
        }
        if( info.getId().equals("alias")){
            return "aliaspass";
        }
        return null;
    }

}
