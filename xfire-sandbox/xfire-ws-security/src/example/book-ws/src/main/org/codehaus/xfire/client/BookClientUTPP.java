package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.demo.Book;
import org.codehaus.xfire.demo.IBook;
import org.codehaus.xfire.security.impl.SecurityProperties;
import org.codehaus.xfire.security.wssecurity.WSS4JOutSecurityHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * @author tomeks
 *  User Token Plain Password
 */
public class BookClientUTPP
{

    protected Map getSecurityProperties()
    {
        Map config = new HashMap();
        config.put(SecurityProperties.PROP_ACTIONS, "usertoken");
        config.put(SecurityProperties.PROP_USER_NAME, "tomek");
        config.put(SecurityProperties.PROP_USER_PASSWORD, "secretPass");
        config.put(SecurityProperties.PROP_USER_PASSWORD_USE_PLAIN, "true");
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

        xfire.addOutHandler(new WSS4JOutSecurityHandler(getSecurityProperties()));

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
