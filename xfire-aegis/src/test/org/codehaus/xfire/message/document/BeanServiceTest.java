package org.codehaus.xfire.message.document;


import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.DefaultService;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.services.SimpleBean;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class BeanServiceTest
    extends AbstractXFireAegisTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        DefaultService service = (DefaultService) 
            getServiceFactory().create(BeanService.class,
                                       "Bean",
                                       "urn:Bean",
                                       Soap11.getInstance(),
                                       SoapConstants.STYLE_DOCUMENT,
                                       SoapConstants.USE_LITERAL, null );

        getServiceRegistry().register( service );

        TypeMapping tm = AegisBindingProvider.getTypeMapping(service);
        tm.register(SimpleBean.class, new QName("urn:Bean", "SimpleBean"), new BeanType());
    }
    
    public void testBeanService() throws Exception
    {
        Document response = 
            invokeService( "Bean", "/org/codehaus/xfire/message/document/bean11.xml" );

        addNamespace( "sb", "urn:Bean" );
        assertValid( "//sb:getSimpleBeanout", response );
        assertValid( "//sb:getSimpleBeanout/sb:howdy[text()=\"howdy\"]", response );
        assertValid( "//sb:getSimpleBeanout/sb:bleh[text()=\"bleh\"]", response );
    }
    
    public void testBeanServiceWSDL() throws Exception
    {
        // Test WSDL generation
        Document doc = getWSDLDocument( "Bean" );

        addNamespace( "wsdl", WSDLWriter.WSDL11_NS );
        addNamespace( "wsdlsoap", WSDLWriter.WSDL11_SOAP_NS );
        addNamespace( "xsd", SoapConstants.XSD );

        assertValid( "/wsdl:definitions/wsdl:types", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]", doc );
        assertValid( "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]", doc );

        assertValid( "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/Bean']", doc );
    }
}