package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * Signature Sample
 * 
 */
public class BookClientSign
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();
        /*
         * config.put(SecurityProperties.PROP_ACTIONS, "signature");
         * config.put(SecurityProperties.PROP_KEYSTORE_PASS, "keystorePass");
         * config.put(SecurityProperties.PROP_KEYSTORE_FILE,
         * "META-INF/xfire/myPrivatestore.jks");
         * config.put(SecurityProperties.PROP_PRIVATE_ALIAS, "alias");
         * config.put(SecurityProperties.PROP_PRIVATE_PASSWORD, "aliaspass");
         */
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
