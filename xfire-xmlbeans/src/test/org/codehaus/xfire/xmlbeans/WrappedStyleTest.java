package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WrappedStyleTest
        extends AbstractXFireTest
{
    private Service endpoint;
    private XmlBeansServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new XmlBeansServiceFactory(getXFire().getTransportManager());
        builder.setStyle(SoapConstants.STYLE_WRAPPED);
        
        endpoint = builder.create(TestService.class,
                                  "TestService",
                                  "urn:TestService",
                                  null);
        
        getServiceRegistry().register(endpoint);
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
