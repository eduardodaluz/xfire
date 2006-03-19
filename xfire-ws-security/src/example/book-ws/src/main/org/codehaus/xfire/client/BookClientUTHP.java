package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.demo.PasswordHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * User Token  (Hashed Password) Sample
 */
public class BookClientUTHP
    extends BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Properties config = new Properties();
        config.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        config.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        config.setProperty(WSHandlerConstants.USER, "alias");
        config.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
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
