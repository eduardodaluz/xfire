package org.codehaus.xfire.fault;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.handler.BadHandler;
import org.codehaus.xfire.service.SimpleService;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * XFireTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XFireFaultTest
    extends AbstractXFireTest
{
    
    public void testFaults()
        throws Exception
    {
        SOAP12FaultHandler soap12 = new SOAP12FaultHandler();
        
        XFireFault fault = new XFireFault(new Exception());
        fault.setSubCode("m:NotAvailable");
        fault.addNamespace("m", "urn:test");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( "Echo",
                                null,
                                out,
                                null,
                                null );
        
        soap12.handleFault( fault, context);
        
        SAXReader reader = new SAXReader();
        Document doc = reader.read( new StringReader(out.toString()) );
        
        addNamespace("s", SOAPConstants.SOAP12_ENVELOPE_NS);
        assertValid("//s:SubCode/s:Value[text()='m:NotAvailable']", doc );
    }
    
    public void testSOAP12()
        throws Exception
    {
        SOAP12FaultHandler soap12 = new SOAP12FaultHandler();
        
        testHandler( soap12 );
    }
    
    public void testSOAP11()
        throws Exception
    {
        SOAP11FaultHandler soap11 = new SOAP11FaultHandler();
        
        testHandler( soap11 );
    }
    
    /**
     * @param soap11
     */
    private void testHandler( FaultHandler soap11 )
    {
        SimpleService service = new SimpleService();
        service.setName("Echo");
        service.setSoapVersion(SOAPConstants.SOAP12_ENVELOPE_NS);
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new BadHandler());
        service.setFaultHandler(soap11);
        
        getServiceRegistry().register(service);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( "Echo",
                                null,
                                out,
                                null,
                                null );
        
        getXFire().invoke(
                getResourceAsStream( "/org/codehaus/xfire/echo11.xml" ),
                context );
        
        System.out.println(out.toString());
    }
}
