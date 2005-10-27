package org.codehaus.xfire.xmlbeans;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedStyleTest
        extends AbstractXmlBeansTest
{
    private Service endpoint;
    private ObjectServiceFactory factory;

    public void setUp()
            throws Exception
    {
        super.setUp();

        factory = (ObjectServiceFactory) getServiceFactory();
        factory.setStyle(SoapConstants.STYLE_WRAPPED);
        
        endpoint = factory.create(TestService.class,
                                  "TestService",
                                  "urn:TestService",
                                  null);
        
        getServiceRegistry().register(endpoint);
    }
    
    public void testParams() throws Exception
    {
        MessagePartInfo info = (MessagePartInfo)
            endpoint.getServiceInfo().getOperation("GetWeatherByZipCode").getInputMessage().getMessageParts().get(0);

        assertEquals(new QName("http://codehaus.org/xfire/xmlbeans", "request"), info.getName());
    }

    public void testInvoke() throws Exception
    {
        Document response = invokeService("TestService", "/org/codehaus/xfire/xmlbeans/WrappedRequest.xml");
        
        assertNotNull(response);

        addNamespace("t", "urn:TestService");
        addNamespace("x", "http://codehaus.org/xfire/xmlbeans");
        assertValid("//t:mixedRequestResponse/x:response/x:form", response);
    }
    
    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument("TestService");

        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:schema[@targetNamespace='urn:TestService']" +
                "/xsd:element[@name='mixedRequest']" +
                "//xsd:element[@name='in0'][@type='xsd:string']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='urn:TestService']" +
                    "/xsd:element[@name='mixedRequest']" +
                    "//xsd:element[@ref='ns1:request']", wsdl);
    }
}
