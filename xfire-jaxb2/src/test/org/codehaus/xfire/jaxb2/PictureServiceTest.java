package org.codehaus.xfire.jaxb2;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.attachments.Attachment;
import org.codehaus.xfire.attachments.Attachments;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.mtom.EchoPicture;
import org.codehaus.xfire.mtom.EchoPictureResponse;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.http.HttpTransport;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PictureServiceTest
        extends AbstractXFireTest
{
    private Service service;
    private ObjectServiceFactory factory;
    private XFireHttpServer server;

    public void setUp()
            throws Exception
    {
        super.setUp();

        factory = new ObjectServiceFactory(getXFire().getTransportManager(),
                                           new AegisBindingProvider(new JaxbTypeRegistry()));
        factory.setStyle(SoapConstants.STYLE_DOCUMENT);
        
        // Set the schemas
        ArrayList<String> schemas = new ArrayList<String>();
        schemas.add(getTestFile("src/test-schemas/PictureService.xsd").getAbsolutePath());
        Map<String,Object> props = new HashMap<String,Object>();
        props.put(ObjectServiceFactory.SCHEMAS, schemas);
        
        service = factory.create(PictureService.class,
                                 "PictureService",
                                 "http://xfire.codehaus.org/mtom",
                                 props);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, PictureServiceImpl.class);
        service.setProperty(SoapConstants.MTOM_ENABLED, "true");
        
        getServiceRegistry().register(service);
        
        server = new XFireHttpServer(getXFire());
        server.start();
    }
    
    
    @Override
    protected void tearDown()
        throws Exception
    {
        server.stop();
    }

    public void testService()
            throws Exception
    {
        OutMessage msg = new OutMessage("http://localhost:8081/PictureService");
        msg.setBody(STAXUtils.createXMLStreamReader(getResourceAsReader("GetPicture.xml")));
        
        // MTOM shouldn't work locally, but things should still work...
        InMessage res = invokeService(msg, HttpTransport.HTTP_BINDING);

        Attachments atts = res.getAttachments();
        Iterator parts = atts.getParts();
        assertTrue(parts.hasNext());
        Attachment att = (Attachment) parts.next();
        
        Document response = (Document) res.getProperty(RESPONSE);
        addNamespace("m", "http://xfire.codehaus.org/mtom");
        addNamespace("xop", "http://www.w3.org/2004/08/xop/include");
        assertValid("//s:Body/m:GetPictureResponse/m:image/xop:Include", response);
    }

    public void testClient()
        throws Exception
    {
        XFireProxyFactory xpf = new XFireProxyFactory();
        PictureService picClient = (PictureService) xpf.create(service, "http://localhost:8081/PictureService");
        
        Client client = ((XFireProxy) Proxy.getInvocationHandler(picClient)).getClient();
        client.addOutHandler(new DOMOutHandler());
        
        EchoPicture req = new EchoPicture();
        req.setImage(ImageIO.read(getTestFile("src/test-resources/xfire.jpg")));
        
        EchoPictureResponse res = picClient.EchoPicture(req);
        assertNotNull(res.getImage());
    }
}
