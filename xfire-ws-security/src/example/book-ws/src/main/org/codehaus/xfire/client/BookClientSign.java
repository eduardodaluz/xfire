package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 *  THIS SAMPLE DOESN'T WORK. THERE IS ALWAYS "The signature verification failed" returned.
 *
 */
public class BookClientSign
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();
       /* config.put(SecurityProperties.PROP_ACTIONS, "signature");
        config.put(SecurityProperties.PROP_KEYSTORE_PASS, "keystorePass");
        config.put(SecurityProperties.PROP_KEYSTORE_FILE, "META-INF/xfire/myPrivatestore.jks");
        config.put(SecurityProperties.PROP_PRIVATE_ALIAS, "alias");
        config.put(SecurityProperties.PROP_PRIVATE_PASSWORD, "aliaspass");*/
        return config;
    }

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        BookClientSign client = new BookClientSign();
        client.executeClient("http://localhost:8080/bookws/services/BookServiceSign");

    }

}
