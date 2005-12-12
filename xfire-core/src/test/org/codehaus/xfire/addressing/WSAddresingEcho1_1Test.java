package org.codehaus.xfire.addressing;

import java.lang.reflect.Method;

import org.codehaus.xfire.DefaultXFire;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.test.EchoImpl;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSAddresingEcho1_1Test
    extends AbstractXFireTest
{

    private static final String SERVICE_NAME="EchoImpl";
    
    private AddressingInData data = null;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        data = new AddressingInData();
        Service service;
        ObjectServiceFactory factory = new ObjectServiceFactory(getXFire().getTransportManager(),
                new MessageBindingProvider())
        {

            protected OperationInfo addOperation(Service endpoint, Method method, String use)
            {
                OperationInfo op = super.addOperation(endpoint, method, use);

                new AddressingOperationInfo("http://example.org/action/echoIn",
                        "http://example.org/action/echoOut", op);

                return op;
            }
        };
        service = factory.create(EchoImpl.class);
        service.addInHandler(new WSATestHandler(data));
        if (getXFire().getInHandlers().size() < 3)
        {
            ((DefaultXFire) getXFire()).addInHandler(new AddressingInHandler());
            ((DefaultXFire) getXFire()).addOutHandler(new AddressingOutHandler());
        }

        getServiceRegistry().register(service);
    }

    public void test1131()
    throws Exception
{
    // A sends a message to B.
    // /soap12:Envelope/soap11:Header/wsa:Action{match}http://example.org/action/echoIn
    // B sends a reply to A.
    // /soap12:Envelope/soap11:Header/wsa:Action{match}http://example.org/action/echoOut

    Document doc = invokeService(SERVICE_NAME,
                                 "/org/codehaus/xfire/addressing/testcases/echo/soap11/message1.xml");

    assertEquals(data.getInHeaders().getAction(), "http://example.org/action/echoIn");
    assertEquals(data.getOutHeaders().getAction(), "http://example.org/action/echoOut");

    XMLOutputter output = new XMLOutputter();
    output.output(doc, System.out);
}
}
