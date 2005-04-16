package org.codehaus.xfire.spring;

/**
 * @author Arjen Poutsma
 */

import java.io.ByteArrayInputStream;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.object.ObjectServiceFactory;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTypeTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.xml.sax.InputSource;

public class XFireExporterTest
        extends AbstractXFireTypeTest
{
    private XFireExporter exporter;

    public void setUp()
            throws Exception
    {
        super.setUp();
        Echo echoBean = new EchoImpl();
        exporter = new XFireExporter();
        exporter.setXfire(getXFire());
        exporter.setServiceInterface(Echo.class);
        exporter.setService(echoBean);
        ServiceFactory serviceFactory = new ObjectServiceFactory(getXFire().getTransportManager(),
                                                                 getRegistry());

        exporter.setServiceFactory(serviceFactory);
    }

    public void testHandleWsdlRequest()
            throws Exception
    {
        exporter.afterPropertiesSet();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "Echo");
        request.addParameter("wsdl", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        exporter.handleRequest(request, response);
        InputSource source = new InputSource(new ByteArrayInputStream(response.getContentAsByteArray()));
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.readWSDL("", source);
    }

    public void testHandleNonDefaultWsdlRequest()
            throws Exception
    {
        String name = "EchoService";
        exporter.setName(name);
        String beanName = "EchoBean";
        exporter.setBeanName(beanName);
        String namespace = "http://tempuri.org";
        exporter.setNamespace(namespace);
        exporter.setStyle(SoapConstants.STYLE_DOCUMENT);
        exporter.setSoapVersion(new Soap11());
        exporter.afterPropertiesSet();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "Echo");
        request.addParameter("wsdl", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        exporter.handleRequest(request, response);
        InputSource source = new InputSource(new ByteArrayInputStream(response.getContentAsByteArray()));
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        Definition definition = reader.readWSDL("", source);
        Service service = definition.getService(new QName(namespace, name));
        assertNotNull(service);
        assertEquals(namespace, service.getQName().getNamespaceURI());
        // The service name should be equal to th
        assertEquals(name, service.getQName().getLocalPart());
        assertFalse(beanName.equals(service.getQName().getLocalPart()));
        Binding binding = definition.getBinding(new QName(namespace, "EchoServiceHttpBinding"));
        assertNotNull(binding);
        SOAPBinding soapBinding = (SOAPBinding) binding.getExtensibilityElements().get(0);
        assertNotNull(soapBinding);
        assertEquals(SoapConstants.STYLE_DOCUMENT, soapBinding.getStyle());
    }

    public void testHandleSoapRequest()
            throws Exception
    {
        exporter.afterPropertiesSet();

        Resource resource = new ClassPathResource("/org/codehaus/xfire/spring/echoRequest.xml");
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "Echo");
        request.setContentType("text/xml");
        request.setContent(bytes);
        MockHttpServletResponse response = new MockHttpServletResponse();
        exporter.handleRequest(request, response);
        System.out.println(response.getContentAsString());


    }
}