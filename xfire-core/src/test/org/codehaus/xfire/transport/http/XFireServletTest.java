package org.codehaus.xfire.transport.http;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.handler.AsyncHandler;
import org.codehaus.xfire.handler.BadHandler;
import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractServletTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.yom.Document;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * XFireServletTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireServletTest
    extends AbstractServletTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        MessageService service = new MessageService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new MockSessionHandler());
        service.setFaultHandler(new Soap12FaultHandler());
        
        getServiceRegistry().register(service);
        
        // A service which throws a fault
        MessageService fault = new MessageService();
        fault.setName("Exception");
        fault.setSoapVersion(Soap12.getInstance());
        fault.setServiceHandler(new SoapHandler(new BadHandler()));
        fault.setFaultHandler(new Soap12FaultHandler());
        
        getServiceRegistry().register(fault);
        
        // Asynchronous service
        MessageService asyncService = new MessageService();
        asyncService.setName("Async");
        asyncService.setSoapVersion(Soap12.getInstance());
        asyncService.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        asyncService.setServiceHandler(new SoapHandler(new AsyncHandler()));
        asyncService.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(asyncService);
    }
    
    public void testServlet() throws Exception
    {
        WebResponse response = newClient().getResponse( "http://localhost/services/Echo?wsdl" );
        
        WebRequest req = new PostMethodWebRequest( "http://localhost/services/Echo",
                getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                "text/xml" );

        response = newClient().getResponse(req);
        
        Document doc = readDocument(response.getText());
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", doc);
        
        assertTrue( MockSessionHandler.inSession );
    }
    
    public void testFaultCode() throws Exception
    {        
        WebRequest req = new PostMethodWebRequest( "http://localhost/services/Exception",
                getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                "text/xml" );

        Transport transport = getXFire().getTransportManager().getTransport(SoapHttpTransport.NAME);
        assertNotNull(transport.getFaultPipeline());
        
        expectErrorCode(req, 500, "Response code 500 required for faults.");
    }
    
    public void testAsync() throws Exception
    {
        WebRequest req = new PostMethodWebRequest( "http://localhost/services/Async",
                getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"),
                "text/xml" );

        WebResponse response = newClient().getResponse(req);
        assertTrue(response.getText().length() == 0);
    }
}
