package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.Soap11FaultHandler;
import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class MustUnderstandTest
    extends AbstractXFireTest
{
    private MessageService service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        service = new MessageService();
        service.setName("Echo");
        service.setSoapVersion(Soap11.getInstance());
 
        service.setServiceHandler(new SoapHandler(new EndpointTestHandler()));
        service.setFaultHandler(new Soap11FaultHandler());

        getServiceRegistry().register(service);
    }
    
    public void testNotUnderstood()
        throws Exception
    {
        Document response = invokeService("Echo", 
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml" );

        assertValid("//s:Fault", response);
    }
    
    public void testRequestUnderstand()
        throws Exception
    {
        HandlerPipeline reqPipeline = new HandlerPipeline();
        UnderstandingHandler handler = new UnderstandingHandler();
        reqPipeline.addHandler(handler);
        service.setRequestPipeline(reqPipeline);
        
        Document response = invokeService("Echo", 
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml" );

        assertNoFault(response);
    }
    
    public class UnderstandingHandler 
        extends AbstractHandler
    {
        public void invoke(MessageContext context)
            throws Exception
        {
        }

        public String[] getRoles()
        {
            return super.getRoles();
        }

        public QName[] getUnderstoodHeaders()
        {
            return new QName[] { new QName("urn:test", "test") };
        }
    }
}
