package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.demo.Book;
import org.codehaus.xfire.demo.IBook;
import org.codehaus.xfire.demo.PasswordHandler;
import org.codehaus.xfire.security.wss4j.WSS4JInHandler;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> 
 * Encryption Sample : Encryption of whole message body
 * 
 * How it works:
 * 
 * Client uses server' public key to encrypt message send to this server.
 * Info about this key is configured at org/codehaus/xfire/client/outsecurity_enc.properties
 * We are also sending user name (client-344-839) along with message, to allow server to identyfy us.
 * Server retirve username inside  ValidateUserTokenHandler class and setup this name on the context 
 * as WSHandlerConstants.ENCRYPTION_USER 
 * so it can be used to retrieve client public key used to encrypt back message.
 * Client uses its own  private key to decrypt response ( configureInProperties method )   
 * 
 */
public class BookClientEnc2Way
    extends BookClient
{

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        new BookClientEnc2Way().executeClient("BookServiceENC2Way");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.xfire.client.BookClient#configureProperties(java.util.Properties)
     */
    protected void configureOutProperties(Properties properties)
    {
        // We want to encrypt message and pass user token to server
        String action = WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.USERNAME_TOKEN;
        properties.setProperty(WSHandlerConstants.ACTION, action);
        // identify ourself ( this will be send as part of user token )
        properties.setProperty(WSHandlerConstants.USER, "client-344-839");
        // set user used to encrypt message
        properties.setProperty(WSHandlerConstants.ENCRYPTION_USER, "serveralias");
        
        // Configuration of public key used to encrypt message goes to
        // properties file.
        properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_enc.properties");
        // We want password hashed
        properties.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // Specyfy callback class to retrive passwords
        properties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class
                .getName());
    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.client.BookClient#getName()
     */
    protected String getName()
    {

        return "Encryption ( 2 directions ) Client";
    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.client.BookClient#executeClient(java.lang.String)
     */
    public void executeClient(String serviceName)
        throws MalformedURLException
    {
        System.out.print("Running client : " + getName() + "\n");

    
        Service serviceModel = new ObjectServiceFactory().create(IBook.class,
                                                                 "BookService",
                                                                 SERVICE_NAMESPACE,
                                                                 null);

        IBook service = (IBook) new XFireProxyFactory().create(serviceModel, SERVICE_URL
                + serviceName);

        Client client = Client.getInstance(service);
       // Client client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();
        client.addOutHandler(new DOMOutHandler());
        Properties outProperties = new Properties();
        configureOutProperties(outProperties);
        client.addOutHandler(new WSS4JOutHandler(outProperties));
        
        // Configure incoming secuirty
        client.addInHandler(new DOMInHandler());
        Properties inProperties = new Properties();
        configureInProperties(inProperties);
        client.addInHandler(new WSS4JInHandler(inProperties));
        
        System.out.print("Looking for isbn : 0123456789 ....");
        Book b = service.findBook("0123456789");
        System.out.print(b.getTitle() + " : " + b.getAuthor() + "\n");

    }

    /**
     * @param inProperties
     */
    private void configureInProperties(Properties inProperties)
    {
        
        inProperties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT );
        inProperties.setProperty(WSHandlerConstants.USER, "client-344-839");
        inProperties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class
                               .getName());
        inProperties.setProperty(WSHandlerConstants.ENABLE_SIGNATURE_CONFIRMATION,"false");
        inProperties.setProperty(WSHandlerConstants.DEC_PROP_FILE,
        "org/codehaus/xfire/client/insecurity_enc.properties");
    }

}
