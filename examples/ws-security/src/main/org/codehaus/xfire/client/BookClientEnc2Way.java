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
        String action = WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.USERNAME_TOKEN;
        properties.setProperty(WSHandlerConstants.ACTION, action);
        properties.setProperty(WSHandlerConstants.USER, "client-344-839");
        properties.setProperty(WSHandlerConstants.ENCRYPTION_USER, "alias");
        
       
        
        // Configuration of public key used to encrypt message goes to
        // properties file.
        properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_enc.properties");
        
        

        properties.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        properties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class
                .getName());
    }

    protected String getName()
    {

        return "Encryption ( 2 directions ) Client";
    }

    public void executeClient(String serviceName)
        throws MalformedURLException
    {
        System.out.print("Running client : " + getName() + "\n");
       // DefaultXFire xfire = (DefaultXFire) XFireFactory.newInstance().getXFire();

    
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

    private void configureInProperties(Properties inProperties)
    {
        
        inProperties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT );
        //inProperties.setProperty(WSHandlerConstants.USER, "alias");
        inProperties.setProperty(WSHandlerConstants.USER, "client-344-839");
        inProperties.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class
                               .getName());
        inProperties.setProperty(WSHandlerConstants.ENABLE_SIGNATURE_CONFIRMATION,"false");
        inProperties.setProperty(WSHandlerConstants.DEC_PROP_FILE,
        "org/codehaus/xfire/client/insecurity_enc.properties");
        //properties.setProperty(WSHandlerConstants.ENCRYPTION_USER, "alias");
        // Configuration of public key used to encrypt message goes to
        // properties file.
        /*inProperties.setProperty(WSHandlerConstants.DEC_PROP_FILE,
                               "org/codehaus/xfire/client/outsecurity_enc.properties");
        */
    }

}
