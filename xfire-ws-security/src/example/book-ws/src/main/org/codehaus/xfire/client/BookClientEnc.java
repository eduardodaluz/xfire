package org.codehaus.xfire.client;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.demo.Book;
import org.codehaus.xfire.demo.IBook;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *  Encryption Sample
 */
public class BookClientEnc
    extends BookClientUTPP
{

    public void executeClient(String url)
        throws MalformedURLException
    {
        DefaultXFire xfire = (DefaultXFire) XFireFactory.newInstance().getXFire();

        Service serviceModel = new ObjectServiceFactory()
                .create(IBook.class, "BookService", "http://xfire.codehaus.org/BookService", null);

        IBook service = (IBook) new XFireProxyFactory().create(serviceModel, url);
        Client client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();
        client.addOutHandler(new DOMOutHandler());
        Properties props = new Properties();

        props.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT);
        props.setProperty(WSHandlerConstants.USER, "alias");
        props.setProperty(WSHandlerConstants.ENC_PROP_FILE,
                          "org/codehaus/xfire/client/outsecurity_enc.properties");

        client.addOutHandler(new WSS4JOutHandler(props));
        Book b = service.findBook("0123456789");

    }

    public static void main(String[] args)
        throws MalformedURLException
    {
        BookClientEnc client = new BookClientEnc();
        client.executeClient("http://localhost:8081/bookws/services/BookServiceENC");

    }

}
