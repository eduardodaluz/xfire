package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.demo.PasswordHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * User Token  (Hashed Password) Sample : Username and password ( in hashed form ) is added to message header.
 * The server side must validate password before processing mesages.
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

    protected void configureOutProperties(Properties properties)
    {
        // Action to perform : user token 
        properties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        // Set password type to hashed
        properties.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // Username in keystore
        properties.setProperty(WSHandlerConstants.USER, "alias");
        // Used do retrive password for given user name
        properties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
        
    }

    protected String getName()
    {
        
        return "User Token ( Hashed Password ) client";
    }

}
