package org.codehaus.xfire.handler.dom;

import java.io.ByteArrayOutputStream;
import org.codehaus.xfire.AbstractXFireTest;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.SOAPConstants;
import org.codehaus.xfire.fault.SOAP12FaultHandler;
import org.codehaus.xfire.service.SimpleService;

/**
 * XFireTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DOMTest
    extends AbstractXFireTest
{
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        SimpleService service = new SimpleService();
        service.setName("Echo");
        service.setSoapVersion(SOAPConstants.SOAP12_ENVELOPE_NS);
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setFaultHandler(new SOAP12FaultHandler());
        
        DOMPipelineHandler pipe = new DOMPipelineHandler();
        pipe.getHandlers().add( new EchoDOMHandler() );
        service.setServiceHandler(pipe);

        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageContext context = 
            new MessageContext( "Echo",
                                null,
                                out,
                                null,
                                null );
        
        getXFire().invoke( getClass().getResourceAsStream("/org/codehaus/xfire/echo11.xml"), 
                           context );
        
        System.out.println( out.toString() );
    }
}
