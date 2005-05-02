package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceEndpointAdapter;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.SOAPBinding;
import org.codehaus.xfire.service.binding.SOAPBindingFactory;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractXFireTest;

/**
 * @author <a href="mailto:dan@envoisolutiosn.com">Dan Diephouse</a>
 */
public class AsyncServiceTest
        extends AbstractXFireTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceInfo serviceInfo = new ServiceInfo(new QName("Async"), getClass());
        SOAPBinding binding = SOAPBindingFactory.createDocumentBinding(new QName("EchoBinding"), Soap12.getInstance());
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo, binding);

        endpoint.setWSDLURL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl").toString());

        endpoint.setServiceHandler(new SoapHandler(new AsyncHandler()));
        endpoint.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(new ServiceEndpointAdapter(endpoint));
    }

    public void testInvoke()
            throws Exception
    {
        assertNull(invokeService("Async", "/org/codehaus/xfire/echo11.xml"));
    }
}
