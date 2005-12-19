package org.codehaus.xfire.message.document;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.services.SimpleBean;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class BeanServiceTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        osf.setStyle(SoapConstants.STYLE_DOCUMENT);
        Service service = getServiceFactory().create(BeanService.class, "Bean", "urn:Bean", null);

        getServiceRegistry().register(service);

        TypeMapping tm = ((AegisBindingProvider) osf.getBindingProvider()).getTypeMapping(service);
        
        BeanType type = new BeanType();
        tm.register(SimpleBean.class, new QName("urn:Bean", "SimpleBean"), type);
        
        ServiceInfo info = service.getServiceInfo();
        OperationInfo o = info.getOperation("getSubmitBean");
        MessageInfo inMsg = o.getInputMessage();
        MessagePartInfo p = inMsg.getMessagePart(new QName("urn:Bean", "bean"));
        p.setSchemaType(type);
        
        o = info.getOperation("getSimpleBean");
        MessageInfo outMsg = o.getOutputMessage();
        p = outMsg.getMessagePart(new QName("urn:Bean", "getSimpleBeanout"));
        p.setSchemaType(type);
    }

    public void testBeanService()
            throws Exception
    {
        Document response =
                invokeService("Bean", "/org/codehaus/xfire/message/document/bean11.xml");

        addNamespace("sb", "urn:Bean");
        assertValid("//sb:getSimpleBeanout", response);
        assertValid("//sb:getSimpleBeanout/sb:howdy[text()=\"howdy\"]", response);
        assertValid("//sb:getSimpleBeanout/sb:bleh[text()=\"bleh\"]", response);
    }

    public void testBeanServiceWSDL()
            throws Exception
    {
        // Test WSDL generation
        Document doc = getWSDLDocument("Bean");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("/wsdl:definitions/wsdl:types", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType", doc);
        assertValid("/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]", doc);
        assertValid(
                "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"bleh\"]",
                doc);
        assertValid(
                "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@name=\"howdy\"]",
                doc);
        assertValid(
                "/wsdl:definitions/wsdl:types/xsd:schema/xsd:complexType[@name=\"SimpleBean\"]/xsd:sequence/xsd:element[@type=\"xsd:string\"]",
                doc);

        assertValid(
                "/wsdl:definitions/wsdl:service/wsdl:port/wsdlsoap:address[@location='http://localhost/services/Bean']",
                doc);
    }
}
