package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * Encryption Sample : Encryption of whole message body 
 */
public class BookClientEnc
    extends BookClient
{

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        new BookClientEnc().executeClient("BookServiceENC");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.client.BookClient#configureProperties(java.util.Properties)
     */
    protected void configureProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
        properties.setProperty(WSHandlerConstants.USER, "alias");
        //Configuration of public key used to encrypt message goes to properties file.
        properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_enc.properties");

    }

    protected String getName()
    {

        return "Encryption Client";
    }

}
