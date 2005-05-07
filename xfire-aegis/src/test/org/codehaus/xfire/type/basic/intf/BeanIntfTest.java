package org.codehaus.xfire.type.basic.intf;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class BeanIntfTest
        extends AbstractXFireAegisTest
{
    public void setUp()
            throws Exception
    {
        super.setUp();

        ServiceEndpoint service = getServiceFactory().create(BeanServiceIntf.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, BeanServiceImpl.class);

        getServiceRegistry().register(service);
    }

    public void testBeanService()
            throws Exception
    {
        Document response =
                invokeService("BeanServiceIntf",
                              "/org/codehaus/xfire/type/basic/intf/getBeanIntf.xml");

        addNamespace("b", "http://intf.basic.type.xfire.codehaus.org");
        assertValid("/s:Envelope/s:Body/b:getBeanIntfResponse", response);
    }

    public void testBeanServiceWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("BeanServiceIntf");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        addNamespace("b", "http://intf.basic.type.xfire.codehaus.org");
        assertValid("//xsd:element[@name='getBeanIntf']", doc);
        assertValid("//xsd:element[@name='getBeanIntfResponse']", doc);
        assertValid("//xsd:complexType[@name='BeanIntf']", doc);
    }

}
