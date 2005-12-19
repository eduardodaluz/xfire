package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.services.ArrayService;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class WrappedAutoTypeTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
        getServiceRegistry().register(getServiceFactory().create(BeanService.class));

        getServiceRegistry().register(getServiceFactory().create(ArrayService.class,
                                                                 "Array",
                                                                 "urn:Array",
                                                                 null));
    }

    public void testBeanService()
            throws Exception
    {
        Document response =
                invokeService("BeanService",
                              "/org/codehaus/xfire/message/wrapped/bean11.xml");

        addNamespace("sb", "http://services.xfire.codehaus.org");
        assertValid("/s:Envelope/s:Body/sb:getSimpleBeanResponse", response);
        assertValid("//sb:getSimpleBeanResponse/sb:out", response);
        assertValid("//sb:getSimpleBeanResponse/sb:out/sb:howdy[text()=\"howdy\"]", response);
        assertValid("//sb:getSimpleBeanResponse/sb:out/sb:bleh[text()=\"bleh\"]", response);
    }

    public void testBeanServiceWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("BeanService");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("/wsdl:definitions/wsdl:types", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']",
                    doc);
        assertValid(
                "//xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']/xsd:element[@name='getSubmitBean']",
                doc);
        assertValid(
                "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='bleh'][@type='xsd:string']",
                doc);
        assertValid(
                "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='bean'][@type='tns:SimpleBean']",
                doc);

        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']" +
                    "/xsd:complexType", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]", doc);

        assertValid(
                "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/BeanService']",
                doc);
    }

    public void testGetArray()
            throws Exception
    {
        Document response = invokeService("Array",
                                          "/org/codehaus/xfire/message/wrapped/GetStringArray11.xml");

        addNamespace("a", "urn:Array");
        addNamespace("sb", "http://test.java.xfire.codehaus.org");
        assertValid("//a:getStringArrayResponse", response);
        assertValid("//a:getStringArrayResponse/a:out/a:string", response);
    }
    
    public void testArrayService()
            throws Exception
    {
        Document response = invokeService("Array",
                                          "/org/codehaus/xfire/message/wrapped/SubmitStringArray11.xml");

        addNamespace("a", "urn:Array");
        addNamespace("sb", "http://test.java.xfire.codehaus.org");
        assertValid("//a:SubmitStringArrayResponse", response);
        assertValid("//a:SubmitStringArrayResponse/a:out[text()='true']", response);
    }

    public void testArrayServiceNoWhitespace()
            throws Exception
    {
        Document response = invokeService("Array",
                                          "/org/codehaus/xfire/message/wrapped/SubmitStringArray11NoWS.xml");

        addNamespace("a", "urn:Array");
        addNamespace("sb", "http://test.java.xfire.codehaus.org");
        assertValid("//a:SubmitStringArrayResponse", response);
        assertValid("//a:SubmitStringArrayResponse/a:out[text()='true']", response);
    }

    public void testArrayServiceWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("Array");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("/wsdl:definitions/wsdl:types", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema[@targetNamespace='urn:Array']", doc);
        assertValid("//xsd:schema[@targetNamespace='urn:Array']/xsd:element[@name='SubmitBeanArray']", doc);
        assertValid(
                "//xsd:element[@name='SubmitStringArray']/xsd:complexType/xsd:sequence/xsd:element[@name='array'][@type='tns:ArrayOfString']",
                doc);
        assertValid(
                "//xsd:element[@name='SubmitBeanArray']/xsd:complexType/xsd:sequence/xsd:element[@name='array'][@type='ns1:ArrayOfSimpleBean']",
                doc);
    }
}
