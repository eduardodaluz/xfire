package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.demo.Book;
import org.codehaus.xfire.demo.IBook;
import org.codehaus.xfire.demo.PasswordHandler;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *  User Token  (Plain Password) Sample
 */
public class BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Properties config = new Properties();
        config.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        config.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        config.setProperty(WSHandlerConstants.USER, "alias");
        config.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName());

        return config;
    }

    /**
     * @param url
     * @throws MalformedURLException
     */
    public void executeClient(String url)
        throws MalformedURLException
    {
        DefaultXFire xfire = (DefaultXFire) XFireFactory.newInstance().getXFire();

        xfire.addOutHandler(new DOMOutHandler());

        xfire.addOutHandler(new WSS4JOutHandler(getSecurityProperties()));

        Service serviceModel = new ObjectServiceFactory()
                .create(IBook.class, "BookService", "http://xfire.codehaus.org/BookService", null);

        IBook service = (IBook) new XFireProxyFactory().create(serviceModel, url);
        Book b = service.findBook("0123456789");

    }

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        BookClientUTPP client = new BookClientUTPP();
        client.executeClient("http://localhost:8081/bookws/services/BookServiceUTPP");

    }

}
