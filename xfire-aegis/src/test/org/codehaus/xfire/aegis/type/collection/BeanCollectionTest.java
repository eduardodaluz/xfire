package org.codehaus.xfire.aegis.type.collection;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class BeanCollectionTest
        extends AbstractXFireAegisTest
{

    public void setUp()
            throws Exception
    {
        super.setUp();
        Service endpoint = getServiceFactory().create(BeanCollectionService.class);
        getServiceRegistry().register(endpoint);
    }

    public void testWSDL() throws Exception
    {
        Document doc =  getWSDLDocument("BeanCollectionService");
        
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:element[@name='strings'][@type='tns:ArrayOfString']", doc);
        assertValid("//xsd:element[@name='doubles'][@type='tns:ArrayOfDouble']", doc);
    }
    
    public static class BeanCollectionService
    {
        public TestBean getBean()
        {
            return null;
        }
    }
}
