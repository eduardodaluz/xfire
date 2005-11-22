package org.codehaus.xfire.client;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;
import org.jdom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class EchoWSDLClientTest
        extends AbstractXFireTest
{
    public void setUp() throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.setStyle(SoapConstants.STYLE_WRAPPED);
        Service service = osf.create(Echo.class, "Echo", "urn:Echo", null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        getServiceRegistry().register(service);
    }

    public void testInvoke()
            throws Exception
    {/*
        WSDLServiceBuilder builder = new WSDLServiceBuilder(getResourceAsStream("/org/codehaus/xfire/wsdl11/echoWrapped.wsdl"));
        builder.setTransportManager(getTransportManager());
        builder.walkTree();
        
        Service service = (Service) builder.getServices().iterator().next();
        String ns = service.getServiceInfo().getName().getNamespaceURI();
        SoapBinding binding = new SoapBinding(new QName(ns, "EchoLocalSoapBinding"),
                                              service);
        binding.setTransport(getTransportManager().getTransport(LocalTransport.BINDING_ID));
        binding.setStyle("wrapped");
        service.addBinding(binding);
        
        Element root = new Element("in0", "a", ns);
        root.addContent("hello");
        
        Client client = new Client(binding, "xfire.local://" + service.getName());
        client.setXFire(getXFire());
        
        OperationInfo op = service.getServiceInfo().getOperation("echo");
        Object[] response = client.invoke(op, new Object[] {root});

        assertNotNull(response);
        assertEquals(1, response.length);
        
        Element e = (Element) response[0];
        assertEquals(root.getName(), e.getName());*/
    }
}
