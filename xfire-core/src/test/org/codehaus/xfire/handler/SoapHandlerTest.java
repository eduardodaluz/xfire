package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class SoapHandlerTest
        extends AbstractXFireTest
{
    private CheckpointHandler reqHandler;
    private CheckpointHandler resHandler;

    public void setUp()
            throws Exception
    {
        super.setUp();
        ServiceInfo serviceInfo = new ServiceInfo(new QName("Echo"), getClass());
        SOAPBinding binding = SOAPBindingFactory.createDocumentBinding(new QName("EchoBinding"), Soap12.getInstance());
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo, binding);
        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        endpoint.setWSDLWriter(writer);

        endpoint.setServiceHandler(new SoapHandler(new EndpointTestHandler()));
        endpoint.setFaultHandler(new Soap12FaultHandler());

        HandlerPipeline reqPipeline = new HandlerPipeline();
        reqHandler = new CheckpointHandler();
        reqPipeline.addHandler(reqHandler);
        endpoint.setRequestPipeline(reqPipeline);

        HandlerPipeline resPipeline = new HandlerPipeline();
        resHandler = new CheckpointHandler();
        resPipeline.addHandler(resHandler);
        endpoint.setResponsePipeline(resPipeline);

        getServiceRegistry().register(endpoint);
    }

    public void testInvoke()
            throws Exception
    {
        Document response = invokeService("Echo", "/org/codehaus/xfire/echo11.xml");

        assertTrue(reqHandler.invoked);
        assertTrue(resHandler.invoked);
    }

    public void testHeaders()
            throws Exception
    {
        Document response = invokeService("Echo", "/org/codehaus/xfire/handler/headerMsg.xml");

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
