package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.fault.Soap12FaultHandler;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.soap.SoapHandler;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;

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
        ServiceEndpoint endpoint = new ServiceEndpoint(serviceInfo);
        WSDLWriter writer = new ResourceWSDL(getClass().getResource("/org/codehaus/xfire/echo11.wsdl"));
        endpoint.setWSDLWriter(writer);

        endpoint.setBinding(new AsyncHandler());
        endpoint.setServiceHandler(new SoapHandler());
        endpoint.setFaultHandler(new Soap12FaultHandler());

        getServiceRegistry().register(endpoint);
    }

    public void testInvoke()
            throws Exception
    {
        assertNull(invokeService("Async", "/org/codehaus/xfire/echo11.xml"));
    }
}
