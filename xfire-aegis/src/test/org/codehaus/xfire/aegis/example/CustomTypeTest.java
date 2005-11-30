package org.codehaus.xfire.aegis.example;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.services.SimpleBean;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class CustomTypeTest
        extends AbstractXFireAegisTest
{
    public void testBeanService()
            throws Exception
    {
        // START SNIPPET: types
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        AegisBindingProvider provider = (AegisBindingProvider) osf.getBindingProvider();
        TypeMapping tm = provider.getTypeMappingRegistry().getDefaultTypeMapping();
        
        // Create your custom type
        BeanType type = new BeanType();
        type.setTypeClass(SimpleBean.class);
        type.setSchemaType(new QName("urn:ReallyNotSoSimpleBean", "SimpleBean"));

        // register the type
        tm.register(type);
        
        Service service = getServiceFactory().create(BeanService.class);

        getServiceRegistry().register(service);

        // END SNIPPET: types
        
        final Document response =
                invokeService("BeanService",
                              "/org/codehaus/xfire/message/wrapped/WrappedCustomTypeTest.bean11.xml");

        addNamespace("sb", "http://services.xfire.codehaus.org");
        assertValid("/s:Envelope/s:Body/sb:getSubmitBeanResponse", response);
        assertValid("//sb:getSubmitBeanResponse/sb:out", response);
        assertValid("//sb:getSubmitBeanResponse/sb:out[text()=\"blah\"]", response);

        final Document doc = getWSDLDocument("BeanService");

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
                "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='in1'][@type='xsd:string']",
                doc);
        assertValid(
                "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='in0'][@type='ns1:SimpleBean']",
                doc);

        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                    "/xsd:complexType", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]", doc);
        assertValid("/wsdl:definitions/wsdl:types" +
                    "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                    "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]", doc);

        assertValid(
                "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/BeanService']",
                doc);
    }
}
