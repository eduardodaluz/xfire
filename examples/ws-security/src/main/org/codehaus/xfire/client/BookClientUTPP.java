package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.demo.PasswordHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * User Token  (Plain Password) Sample : Add username and password ( as plain text, not hashed ) to message header.
 */
public class BookClientUTPP
    extends BookClient
{

   //	START SNIPPET: uttp
    protected void configureOutProperties(Properties config)
    {
        // Action to perform : user token
        config.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        // Password type : plain text
        config.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        // for hashed password use:
        //properties.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // User name to send
        config.setProperty(WSHandlerConstants.USER, "alias");
        // Callback used to retrive password for given user.
        config.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
    }
    //  END SNIPPET: uttp
    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
         new BookClientUTPP().executeClient("BookServiceUTPP");
        

    }

    protected String getName()
    {
        
        return "User Token ( Plain Password ) Client";
    }

}
