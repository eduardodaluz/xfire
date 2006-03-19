package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * Timestamp example
 */
public class BookClientTS
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();

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
