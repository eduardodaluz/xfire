package org.codehaus.xfire;

import java.io.ByteArrayOutputStream;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.handler.EchoHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.WSDLWriter;
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
        
        MessageService service = new MessageService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new EchoHandler());
        service.setFaultHandler(new Soap12FaultHandler());
        
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
        
        WSDLWriter wsdl = service.getWSDLWriter();
        
        assertNotNull(wsdl);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        getXFire().generateWSDL("Echo", out);
    }
}
