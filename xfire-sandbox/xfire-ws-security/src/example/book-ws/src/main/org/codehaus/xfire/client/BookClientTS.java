package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.security.impl.SecurityProperties;

/**
 * @author tomeks
 * 
 * Timestamp example
 */
public class BookClientTS
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();
        // Add timestamp header
        config.put(SecurityProperties.PROP_ACTIONS, "timestamp");
        // Message is valid for 10 seconds.
        config.put(SecurityProperties.PROP_TIME_TO_LIVE, "10");

        return config;
    }

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        BookClientTS client = new BookClientTS();
        client.executeClient("http://localhost:8081/bookws/services/BookServiceTS");

    }
}
