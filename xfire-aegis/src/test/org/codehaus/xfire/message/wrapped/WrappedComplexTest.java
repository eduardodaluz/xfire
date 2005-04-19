package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.services.ComplexService;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class WrappedComplexTest
    extends AbstractXFireAegisTest
{
    ObjectService service;

    public void setUp()
        throws Exception
    {
        super.setUp();

        service = (ObjectService) getServiceFactory().create(ComplexService.class);

        getServiceRegistry().register(service);
    }

    /**
     * Tests to make sure that when we have types in all sorts of different
     * namespaces wsdl is generated correctly.
     * 
     * @throws Exception
     */
    public void testBeanServiceWSDL()
        throws Exception
    {
        final Document doc = getWSDLDocument("ComplexService");
        printNode(doc);

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        String ns1 = "http://ns1.services.xfire.codehaus.org";
        String ns2 = "http://ns2.services.xfire.codehaus.org";
        String root = "http://services.xfire.codehaus.org";

        String ns1p = doc.getRootElement().getNamespacePrefix(ns1);
        String ns2p = doc.getRootElement().getNamespacePrefix(ns2);
        String rootp = "r";

        addNamespace(ns1p, ns1);
        addNamespace(ns2p, ns2);
        addNamespace(rootp, root);

        assertValid("//xsd:schema[@targetNamespace='" + ns2
                + "']/xsd:complexType[@name='Complex2']", doc);
                
        assertValid("//xsd:schema[@targetNamespace='" + ns1
                + "']/xsd:complexType[@name='Complex1']" + "/xsd:sequence/xsd:element[@type='"
                + ns2p + ":Complex2']", doc);
        
        assertValid("//xsd:schema[@targetNamespace='" + root
                + "']/xsd:element[@name='getComplex1Response']"
                + "/xsd:complexType/xsd:sequence/xsd:element[@name='out'][@type='" + ns1p
                + ":Complex1']", doc);
    }
}
