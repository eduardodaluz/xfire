package org.codehaus.xfire.message.document;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class DocumentServiceTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceEndpoint service = getServiceFactory().create(DocumentService.class,
                                                             "Doc",
                                                             "urn:Doc",
                                                             Soap11.getInstance(),
                                                             SoapConstants.STYLE_DOCUMENT,
                                                             SoapConstants.USE_LITERAL, null);

        getServiceRegistry().register(service);
    }

    public void testNoParams()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-1.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString1out", response);
        assertValid("//d:getString1out[text()=\"string\"]", response);
    }

    public void testOneParam()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-2.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString2out", response);
        assertValid("//d:getString2out[text()=\"bleh\"]", response);
    }

    public void testTwoParams()
            throws Exception
    {
        Document response =
                invokeService("Doc", "/org/codehaus/xfire/message/document/document11-3.xml");

        addNamespace("d", "urn:Doc");
        assertValid("//d:getString3out", response);
        assertValid("//d:getString3out[text()=\"blehbleh2\"]", response);
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
