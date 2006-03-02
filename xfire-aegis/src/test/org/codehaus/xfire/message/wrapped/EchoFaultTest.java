package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Document;

public class EchoFaultTest
    extends AbstractXFireAegisTest
{
    private Service service;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(EchoWithFault.class, "EchoWithFault", "urn:xfire:echo", null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoWithFaultImpl.class);
        
        getServiceRegistry().register(service);
    }

    public void testService() throws Exception
    {
        Document response = invokeService("EchoWithFault", "echoWithFault.xml");

        addNamespace("e", "urn:xfire:echo");
        addNamespace("f", "http://wrapped.message.xfire.codehaus.org");
        
        assertValid("//detail/e:EchoFault/f:customMessage[text()='yo']", response);
    }
    
    public void testClient() throws Exception
    {
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        EchoWithFault echo = (EchoWithFault) factory.create(service, "xfire.local://EchoWithFault");
        
        try
        {
            echo.echo("yo");
            fail("Should have thrown custom fault.");
        }
        catch (EchoFault fault)
        {
            assertEquals("message", fault.getMessage());
            assertEquals("yo", fault.getCustomMessage());
        }
        catch (Throwable e)
        {
        }
    }
    
    public void testFaultWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        String ns = service.getTargetNamespace();
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("w", WSDLBuilder.WSDL11_NS);
        addNamespace("ws", WSDLBuilder.WSDL11_SOAP_NS);
        
        assertValid("//xsd:schema[@targetNamespace='" + ns + "']/xsd:element[@name='EchoFault']", wsdl);
        assertValid("//w:message[@name='EchoFault']/w:part[@name='EchoFault'][@element='tns:EchoFault']", wsdl);
        assertValid("//w:portType[@name='EchoWithFaultPortType']/w:operation[@name='echo']" +
                "/w:fault[@name='EchoFault']", wsdl);
        assertValid("//w:binding[@name='EchoWithFaultHttpBinding']/w:operation[@name='echo']" +
                    "/w:fault[@name='EchoFault']/ws:fault[@name='EchoFault'][@use='literal']", wsdl);
    }
}
