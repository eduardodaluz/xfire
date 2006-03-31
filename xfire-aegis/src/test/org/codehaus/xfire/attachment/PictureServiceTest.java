package org.codehaus.xfire.attachment;

import java.lang.reflect.Proxy;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.util.dom.DOMOutHandler;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PictureServiceTest
        extends AbstractXFireAegisTest
{
    private Service service;
    private XFireHttpServer server;

    public void setUp()
            throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(PictureService.class,
                                 "PictureService",
                                 "http://xfire.codehaus.org/mtom",
                                 null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, PictureServiceImpl.class);
        service.setProperty(SoapConstants.MTOM_ENABLED, "true");
        
        getServiceRegistry().register(service);
        
        server = new XFireHttpServer(getXFire());
        server.start();
    }

    protected void tearDown()
        throws Exception
    {
        server.stop();
    }

    public void testClientServer()
        throws Exception
    {
        XFireProxyFactory xpf = new XFireProxyFactory();
        PictureService picClient = (PictureService) xpf.create(service, "http://localhost:8081/PictureService");
        
        Client client = ((XFireProxy) Proxy.getInvocationHandler(picClient)).getClient();
        client.setProperty(SoapConstants.MTOM_ENABLED, "true");
        client.addOutHandler(new DOMOutHandler());
        
        DataSource source = picClient.GetPicture();
        
        FileDataSource fileSource = new FileDataSource(getTestFile("src/test-resources/xfire.jpg"));
        DataSource source2 = picClient.EchoPicture(fileSource);
        assertNotNull(source2);
        
        DataHandler handler = new DataHandler(source2);
        DataHandler handler2 = picClient.EchoPicture2(handler);
        assertNotNull(handler2);
    }
}
