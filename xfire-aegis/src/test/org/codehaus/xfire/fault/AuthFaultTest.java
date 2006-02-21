package org.codehaus.xfire.fault;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;
import org.jdom.Document;

public class AuthFaultTest
    extends AbstractXFireAegisTest
{
    private Service service;
    private String ns = "urn:xfire:authenticate";
    private String name = "AuthService";

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(AuthService.class, name, ns, null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, AuthServiceImpl.class);
        
        getServiceRegistry().register(service);
    }

    public void testService() throws Exception
    {
        Document response = invokeService(name, "authenticate.xml");

        addNamespace("f", "urn:xfire:authenticate:fault");
        assertValid("//detail/f:AuthenticationFault/f:message[text()='Invalid username/password']", response);
    }
    
    public void testClient() throws Exception
    {
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        AuthService echo = (AuthService) factory.create(service, "xfire.local://AuthService");
        
        try
        {
            echo.authenticate("yo", "yo");
            fail("Should have thrown custom fault.");
        }
        catch (AuthenticationFault_Exception fault)
        {
            assertEquals("Invalid username/password", fault.getFaultInfo().getMessage());
            assertEquals("message", fault.getMessage());
        }
    }
    
    public void testFaultWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        String ns = service.getTargetNamespace();
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("w", WSDLBuilder.WSDL11_NS);
        addNamespace("ws", WSDLBuilder.WSDL11_SOAP_NS);
        
        assertValid("//xsd:schema[@targetNamespace='urn:xfire:authenticate:fault']" +
                "/xsd:element[@name='AuthenticationFault']", wsdl);
        assertValid("//w:message[@name='AuthenticationFault']" +
                "/w:part[@name='AuthenticationFault'][@element='ns1:AuthenticationFault']", wsdl);
        assertValid("//w:portType[@name='AuthServicePortType']/w:operation[@name='authenticate']" +
                "/w:fault[@name='AuthenticationFault']", wsdl);
        assertValid("//w:binding[@name='AuthServiceHttpBinding']/w:operation[@name='authenticate']" +
                    "/w:fault[@name='AuthenticationFault']/ws:fault[@name='AuthenticationFault'][@use='literal']", wsdl);
    }
}
