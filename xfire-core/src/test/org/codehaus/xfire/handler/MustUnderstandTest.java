package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.Soap11FaultHandler;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class MustUnderstandTest
        extends AbstractXFireTest
{
    private ServiceEndpoint endpoint;

    public void setUp()
            throws Exception
    {
        super.setUp();


        ServiceInfo service = new ServiceInfo(new QName("Echo"), getClass());
        endpoint = new ServiceEndpoint(service);

        endpoint.setBinding(new EndpointTestHandler());
        endpoint.setServiceHandler(new SoapHandler());
        endpoint.setFaultHandler(new Soap11FaultHandler());

        getServiceRegistry().register(endpoint);
    }

    public void testNotUnderstood()
            throws Exception
    {
        Document response = invokeService("Echo",
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml");

        assertValid("//s:Fault", response);
    }

    public void testRequestUnderstand()
            throws Exception
    {
        HandlerPipeline reqPipeline = new HandlerPipeline();
        UnderstandingHandler handler = new UnderstandingHandler();
        reqPipeline.addHandler(handler);
        endpoint.setRequestPipeline(reqPipeline);

        Document response = invokeService("Echo",
                                          "/org/codehaus/xfire/handler/mustUnderstand.xml");

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
            return new QName[]{new QName("urn:test", "test")};
        }
    }
}
