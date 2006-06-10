package org.codehaus.xfire.demo;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;

/**
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 */
public class BookServiceTest extends AbstractXFireAegisTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        // Register the web service.
        XFire xfire = getXFire();
        ServiceFactory factory = new ObjectServiceFactory(xfire.getTransportManager(), null);
        
        Service service = factory.create(BookService.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, BookServiceImpl.class);
        xfire.getServiceRegistry().register(service);
    }
    
    public void testClient() throws Exception
    {
        Service serviceModel = getXFire().getServiceRegistry().getService("BookService");
        XFireProxyFactory serviceFactory = new XFireProxyFactory(getXFire());
        
        // Connect to web service.
        BookService service = (BookService) serviceFactory.create(serviceModel, "xfire.local://BookService");
        assertNotNull(service);
        
        // Call web service.
        Book[] books = service.getBooks();
        assertNotNull(books);
    }
}
