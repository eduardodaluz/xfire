package org.codehaus.xfire.message.wrapped;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.services.DataService;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class ByteDataTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();
        getServiceRegistry().register(getServiceFactory().create(DataService.class));
    }

    public void testBeanService()
            throws Exception
    {
        Document response =
                invokeService("DataService",
                              "/org/codehaus/xfire/message/wrapped/GetData.xml");

        printNode(response);
    }

    public void testBeanServiceWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("DataService");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("//xsd:element[@name='data'][@type='xsd:base64Binary']", doc);
    }
}
