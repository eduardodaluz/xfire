package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.security.impl.SecurityProperties;

/**
 * @author tomeks User Token Hashed Password
 */
public class BookClientUTHP
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();
        config.put(SecurityProperties.PROP_ACTIONS, "usertoken");
        config.put(SecurityProperties.PROP_TIME_TO_LIVE, "1");
        return config;
    }

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        BookClientUTHP client = new BookClientUTHP();
        client.executeClient("http://localhost:8081/bookws/services/BookServiceUTHP");

    }

}
