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
    extends BookClient
{


    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        new BookClientUTHP().executeClient("BookServiceUTHP");

    }

    protected void configureProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        properties.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        properties.setProperty(WSHandlerConstants.USER, "alias");
        properties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
        
    }

    protected String getName()
    {
        
        return "User Token ( Hashed Password ) client";
    }

}
