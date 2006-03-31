package org.codehaus.xfire.client;

import java.net.MalformedURLException;
import java.util.Properties;

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
 * Base class for security samples.
 * 
 */
public abstract class BookClient
{
    

    public static final String SERVICE_NAMESPACE = "http://xfire.codehaus.org/BookService";

    public static final String SERVICE_URL = "http://localhost:8081/bookws/services/";

    /**
     * Setups required security properties for given test.
     * @param properties
     */
    abstract protected void configureProperties(Properties properties);
    
    /**
     * @return Name of the client.
     */
    abstract protected String getName();
    /**
     * @param url
     * @throws MalformedURLException
     */
    public void executeClient(String serviceName)
        throws MalformedURLException
    {
        System.out.print("Running client : "+getName()+"\n");
        DefaultXFire xfire = (DefaultXFire) XFireFactory.newInstance().getXFire();

        xfire.addOutHandler(new DOMOutHandler());
        Properties properties = new Properties();
        configureProperties(properties);
        xfire.addOutHandler(new WSS4JOutHandler(properties));

        Service serviceModel = new ObjectServiceFactory().create(IBook.class,
                                                                 "BookService",
                                                                 SERVICE_NAMESPACE,
                                                                 null);

        IBook service = (IBook) new XFireProxyFactory().create(serviceModel, SERVICE_URL
                + serviceName);

        System.out.print("Looking for isbn : 0123456789 ....");
        Book b = service.findBook("0123456789");
        System.out.print(b.getTitle() + " : " + b.getAuthor() + "\n");

    }

}