package org.codehaus.xfire;

import java.io.ByteArrayOutputStream;
import org.codehaus.xfire.fault.SOAP12FaultHandler;
import org.codehaus.xfire.handler.EchoHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.SimpleService;
import org.codehaus.xfire.wsdl.WSDL;
import org.dom4j.Document;

/**
 * XFireTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireTest
    extends AbstractXFireTest
{
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        SimpleService service = new SimpleService();
        service.setName("Echo");
        service.setSoapVersion(SOAPConstants.SOAP12_ENVELOPE_NS);
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new EchoHandler());
        service.setFaultHandler(new SOAP12FaultHandler());
        
        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        Document response = invokeService( "Echo", "/org/codehaus/xfire/echo11.xml" );
        
        addNamespace("m", "urn:Echo");
        assertValid("//m:echo", response);
    }
    
    public void testWSDL()
        throws Exception
    {
        Service service = (Service) getServiceRegistry().getService("Echo");
        
        WSDL wsdl = service.getWSDL();
        
        assertNotNull(wsdl);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getXFire().generateWSDL("Echo", out);
    }
}
