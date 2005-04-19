package org.codehaus.xfire.message.wrapped;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.services.SimpleBean;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class WrappedCustomTypeTest 
    extends AbstractXFireAegisTest
{
    ObjectService service;
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = (ObjectService) getServiceFactory().create(BeanService.class);

        getServiceRegistry().register( service );

        TypeMapping tm = AegisBindingProvider.getTypeMapping(service);
        tm.register(SimpleBean.class,
                    new QName("urn:ReallyNotSoSimpleBean", "SimpleBean"),
                    new BeanType());
    }

    public void testBeanService() throws Exception
    {
        final Document response =
            invokeService( "BeanService",
                           "/org/codehaus/xfire/message/wrapped/WrappedCustomTypeTest.bean11.xml" );

        printNode( response );

        addNamespace( "sb", "http://services.xfire.codehaus.org" );
        assertValid( "/s:Envelope/s:Body/sb:getSubmitBeanResponse", response );
        assertValid( "//sb:getSubmitBeanResponse/sb:out", response );
        assertValid( "//sb:getSubmitBeanResponse/sb:out[text()=\"blah\"]", response );
    }

    public void testBeanServiceWSDL() throws Exception
    {
        final Document doc = getWSDLDocument( "BeanService" );

        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

        assertValid( "/wsdl:definitions/wsdl:types", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']",
                     doc );
        assertValid(
            "//xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']/xsd:element[@name='getSubmitBean']",
            doc );
        assertValid(
            "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='in1'][@type='xsd:string']",
            doc );
        assertValid(
            "//xsd:element[@name='getSubmitBean']/xsd:complexType/xsd:sequence/xsd:element[@name='in0'][@type='ns1:SimpleBean']",
            doc );

        assertValid( "/wsdl:definitions/wsdl:types" +
                     "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                     "/xsd:complexType", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                     "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                     "/xsd:complexType[@name=\"SimpleBean\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                     "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                     "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                     "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                     "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types" +
                     "/xsd:schema[@targetNamespace='urn:ReallyNotSoSimpleBean']" +
                     "/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]", doc );

        assertValid(
            "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/BeanService']",
            doc );
    }
}
