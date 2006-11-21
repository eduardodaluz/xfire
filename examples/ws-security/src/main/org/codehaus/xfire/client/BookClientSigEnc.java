package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class BookClientSigEnc
    extends BookClient
{

//  START SNIPPET: enc_sig
    protected void configureOutProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT + " "
                + WSHandlerConstants.SIGNATURE);
        // User in keystore
        properties.setProperty(WSHandlerConstants.USER, "client-344-839");
        properties.setProperty(WSHandlerConstants.ENCRYPTION_USER, "serveralias");
        // This callback is used to specify password for given user for keystore
        properties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,
                               org.codehaus.xfire.demo.PasswordHandler.class.getName());
        // Configuration for accessing private key in keystore
        properties.setProperty(WSHandlerConstants.SIG_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_sign.properties");
        properties.setProperty(WSHandlerConstants.SIG_KEY_ID, "IssuerSerial");
        properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_enc.properties");
    }
    //  END SNIPPET: enc_sig
    protected String getName()
    {
        return "Signature Encryption Client";
    }

    public static void main(String[] args)
        throws MalformedURLException
    {
        new BookClientSigEnc().executeClient("BookServiceSIGENC");
    }

}
