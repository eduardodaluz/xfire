package org.codehaus.xfire.attachment;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.codehaus.xfire.transport.http.HttpTransport;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PictureServiceTest
        extends AbstractXFireAegisTest
{
    private Service service;
    private XFireHttpServer server;
    private Client client;
    private PictureService picClient;
    
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
        server.setPort(8082);
        server.start();

        XFireProxyFactory xpf = new XFireProxyFactory();
        picClient = (PictureService) xpf.create(service, "http://localhost:8082/PictureService");
        
        client = ((XFireProxy) Proxy.getInvocationHandler(picClient)).getClient();
        client.setProperty(HttpTransport.CHUNKING_ENABLED, "true");
    }

    protected void tearDown()
        throws Exception
    {
        server.stop();
    }

    public void testClientServer()
        throws Exception
    {
        client.setProperty(SoapConstants.MTOM_ENABLED, "true");
        client.setProperty(HttpTransport.CHUNKING_ENABLED, "true");
        
        DataSource source = picClient.GetPicture();
        assertNotNull(source);
        
        DataSource pbsource = picClient.GetPictureBean().getData();
        assertNotNull(pbsource);
        
        InputStream is = pbsource.getInputStream();
        assertNotNull(is);
        
        FileDataSource fileSource = new FileDataSource(getTestFile("src/test-resources/xfire.jpg"));
        DataSource source2 = picClient.EchoPicture(fileSource);
        assertNotNull(source2);
        
        DataHandler handler = new DataHandler(source2);
        DataHandler handler2 = picClient.EchoPicture2(handler);
        assertNotNull(handler2);
        
        byte[] picBytes = readAsBytes(new FileInputStream(getTestFile("src/test-resources/xfire.jpg")));
        byte[] response = picClient.EchoPictureBytes(picBytes);
        
        assertEquals(picBytes.length, response.length);
    }
    
    public void testClientServerNoMTOM()
        throws Exception
    {
        byte[] picBytes = readAsBytes(new FileInputStream(getTestFile("src/test-resources/xfire.jpg")));
        byte[] response = picClient.EchoPictureBytes(picBytes);
        
        assertEquals(picBytes.length, response.length);
    }
    
    public void testAttachmentArray()
        throws Exception
    {
        client.setProperty(SoapConstants.MTOM_ENABLED, "true");

        DataHandler handler = createDataHandler();
        DataHandler handler2 = createDataHandler();

        DataHandler[] handlers = picClient.EchoPictureArray(new DataHandler[] { handler, handler2 });
        assertEquals(2, handlers.length);
    }

    private DataHandler createDataHandler() 
    {
        FileDataSource fileSource = new FileDataSource(getTestFile("src/test-resources/xfire.jpg"));
        DataHandler handler = new DataHandler(fileSource);
        return handler;
    }
    
    public static byte[] readAsBytes(InputStream input) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            final byte[] buffer = new byte[8096];

            int n = 0;
            while (-1 != (n = input.read(buffer)))
            {
                output.write(buffer, 0, n);
            }
        }
        finally
        {
            output.close();
            input.close();
        }
        return output.toByteArray();
    }
}
