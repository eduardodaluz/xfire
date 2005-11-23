package org.codehaus.xfire.message.document;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class ProviderServiceTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_DOCUMENT);
        Service service = getServiceFactory().create(ProviderService.class);

        getServiceRegistry().register(service);
    }

    public void testNoParams()
            throws Exception
    {
        Document response =
                invokeService("ProviderService", "/org/codehaus/xfire/message/document/document11-2.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//s:Body/d:getString2in0", response);
    }

    /*
    public void testBeanServiceWSDL() throws Exception
    {
        // Test WSDL generation
        Document doc = getWSDLDocument( "Bean" );

        addNamespace( "wsdl", WSDL.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDL.WSDL11_SOAP_NS );
        addNamespace( "xsd", SOAPConstants.XSD );

        assertValid( "/wsdl:definitions/wsdl:types", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType[@name=\"SimpleBean\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                "/xsd:schema[@targetNamespace='http://test.java.xfire.codehaus.org']" +
                "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]", doc );

        assertValid( "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location=\"http://localhost/services/Bean\"]", doc );
    }*/
}
