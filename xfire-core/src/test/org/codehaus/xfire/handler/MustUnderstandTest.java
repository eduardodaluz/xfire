package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

public class MustUnderstandTest
        extends AbstractXFireTest
{
    private Service endpoint;

    public void setUp()
            throws Exception
    {
        super.setUp();

        endpoint = getServiceFactory().create(Echo.class,
                                              Soap11.getInstance(),
                                              SoapConstants.STYLE_MESSAGE,
                                              SoapConstants.USE_LITERAL);
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
        endpoint.setInPipeline(reqPipeline);

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
