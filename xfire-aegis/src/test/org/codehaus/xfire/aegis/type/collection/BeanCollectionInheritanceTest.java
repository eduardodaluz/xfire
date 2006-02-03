package org.codehaus.xfire.aegis.type.collection;

import java.util.Collection;
import java.util.Map;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class BeanCollectionInheritanceTest
        extends AbstractXFireAegisTest
{

    public void setUp()
            throws Exception
    {
        super.setUp();
        Service endpoint = getServiceFactory().create(BeanCollectionInheritanceService.class);
        getServiceRegistry().register(endpoint);
    }

    public void testWSDL() throws Exception
    {
        Document doc =  getWSDLDocument("BeanCollectionInheritanceService");
        
        printNode(doc);
        
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:element[@name='strings'][@type='tns:ArrayOfString']", doc);
        assertValid("//xsd:element[@name='doubles'][@type='tns:ArrayOfDouble']", doc);
    }
        
    public static class BeanCollectionInheritanceService
    {
        public Collection getInheritedBeanCollection()
        {
            return null;
        }
        
        public Map getInheritedBeanMap()
        {
        	return null;
        }
    }
}
