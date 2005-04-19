package org.codehaus.xfire.wsdl11;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.services.BeanService;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderInfo;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class WSDLBuilderInfoTest
    extends AbstractXFireAegisTest
{
    public void setUp()
        throws Exception
    {
        super.setUp();

        Service service = getServiceFactory().create(BeanService.class);

        WSDLBuilderInfo info = new WSDLBuilderInfo(service);
        info.setPortType("MyPortType");
        info.setServiceName("MyServiceName");
        info.setTargetNamespace("urn:my:namespace");
        service.setProperty(WSDLBuilderInfo.KEY, info);

        getServiceRegistry().register(service);
    }

    public void testBeanServiceWSDL()
        throws Exception
    {
        Document doc = getWSDLDocument("BeanService");
        printNode(doc);

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("/wsdl:definitions/wsdl:types/xsd:schema[@targetNamespace='urn:my:namespace']",
                    doc);
        assertValid("//xsd:schema[@targetNamespace='urn:my:namespace']/xsd:element[@name='getSubmitBean']",
                    doc);

        assertValid("/wsdl:definitions/wsdl:types"
                + "/xsd:schema[@targetNamespace='http://services.xfire.codehaus.org']"
                + "/xsd:complexType[@name=\"SimpleBean\"]", doc);

        assertValid("/wsdl:definitions/wsdl:portType[@name='MyPortType']", doc);
        assertValid("/wsdl:definitions/wsdl:binding[@type='tns:MyPortType']", doc);
        assertValid("/wsdl:definitions/wsdl:service[@name='MyServiceName']", doc);
    }
}
