package org.codehaus.xfire.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class SoapHandlerTest
    extends AbstractXFireTest
{
    private CheckpointHandler reqHandler;
    private CheckpointHandler resHandler;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        MessageService service = new MessageService();
        service.setName("Echo");
        service.setSoapVersion(Soap12.getInstance());
        service.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());
        
        service.setServiceHandler(new SoapHandler(new EndpointTestHandler()));
        service.setFaultHandler(new Soap12FaultHandler());
        
        HandlerPipeline reqPipeline = new HandlerPipeline();
        reqHandler = new CheckpointHandler();
        reqPipeline.addHandler(reqHandler);
        service.setRequestPipeline(reqPipeline);
        
        HandlerPipeline resPipeline = new HandlerPipeline();
        resHandler = new CheckpointHandler();
        resPipeline.addHandler(resHandler);
        service.setResponsePipeline(resPipeline);
        
        getServiceRegistry().register(service);
    }
    
    public void testInvoke()
        throws Exception
    {
        Document response = invokeService( "Echo", "/org/codehaus/xfire/echo11.xml" );
        
        assertTrue(reqHandler.invoked);
        assertTrue(resHandler.invoked);
    }
    
    public void testHeaders()
        throws Exception
    {
        Document response = invokeService( "Echo", "/org/codehaus/xfire/handler/headerMsg.xml" );
        
        assertTrue(reqHandler.invoked);
        assertTrue(resHandler.invoked);
        addNamespace("e", "urn:Echo");
        assertValid("/s:Envelope/s:Header/e:echo", response);
    }
    
    public class CheckpointHandler
        extends AbstractHandler
    {
        public boolean invoked = false;

        public void invoke(MessageContext context)
            throws Exception
        {
            this.invoked = true;
        }
    }
}
