package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *  Encryption Sample
 */
public class BookClientEnc
    extends BookClient
{

    /*public void executeClient(String url)
        throws MalformedURLException
    {
        DefaultXFire xfire = (DefaultXFire) XFireFactory.newInstance().getXFire();

        Service serviceModel = new ObjectServiceFactory()
                .create(IBook.class, "BookService", "http://xfire.codehaus.org/BookService", null);

        IBook service = (IBook) new XFireProxyFactory().create(serviceModel, url);
        Client client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();
        client.addOutHandler(new DOMOutHandler());
        Properties props = new Properties();

        
        client.addOutHandler(new WSS4JOutHandler(props));
        Book b = service.findBook("0123456789");

    }
*/
    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        
        new BookClientEnc().executeClient("BookServiceENC");

    }

    /* (non-Javadoc)
     * @see org.codehaus.xfire.client.BookClient#configureProperties(java.util.Properties)
     */
    protected void configureProperties(Properties properties)
    {
        properties.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
        properties.setProperty(WSHandlerConstants.USER, "alias");
        properties.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                          "org/codehaus/xfire/client/outsecurity_enc.properties");

        
    }

    protected String getName()
    {
        
        return "Encryption Client";
    }

}
