package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.demo.PasswordHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * User Token  (Plain Password) Sample
 */
public class BookClientUTPP
    extends BookClient
{

    protected void configureProperties(Properties config)
    {
        config.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        config.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        config.setProperty(WSHandlerConstants.USER, "alias");
        config.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());
    }

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
