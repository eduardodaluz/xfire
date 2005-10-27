package org.codehaus.xfire.aegis;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 21, 2004
 */
public class InnerClassTest
        extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp()
            throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(InnerService.class);
        getServiceRegistry().register(service);
    }

    public void testInnerBeanService()
            throws Exception
    {
        assertEquals("InnerService", service.getName());
        
        Document response =
                invokeService("InnerService",
                              "/org/codehaus/xfire/aegis/getInnerBean.xml");

        addNamespace("a", "http://aegis.xfire.codehaus.org");
        assertValid("//a:getInnerBeanResponse", response);
        assertValid("//a:getInnerBeanResponse/a:out", response);
        assertValid("//a:getInnerBeanResponse/a:out/a:world[text()=\"hello\"]", response);
    }

    public void testBeanServiceWSDL()
            throws Exception
    {
        Document doc = getWSDLDocument("InnerService");

        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("wsdlsoap", WSDLWriter.WSDL11_SOAP_NS);
        addNamespace("xsd", SoapConstants.XSD);

        assertValid("//xsd:schema[@targetNamespace='http://aegis.xfire.codehaus.org']", doc);
        assertValid("//xsd:schema[@targetNamespace='http://aegis.xfire.codehaus.org']"+
                    "/xsd:complexType[@name='InnerBean']", doc);
    }

    public static class InnerService
    {
        public InnerBean getInnerBean()
        {
            InnerBean bean = new InnerBean();
            bean.setWorld("hello");
            return bean;
        }
    }
    
    public static class InnerBean
    {
        private String world;

        public String getWorld()
        {
            return world;
        }

        public void setWorld(String world)
        {
            this.world = world;
        }
        
    }
}
