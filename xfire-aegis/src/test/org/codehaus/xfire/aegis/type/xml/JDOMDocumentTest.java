package org.codehaus.xfire.aegis.type.xml;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class JDOMDocumentTest extends AbstractXFireAegisTest
{
    public void testType() throws Exception
    {
        ((ObjectServiceFactory) getServiceFactory()).setStyle(SoapConstants.STYLE_DOCUMENT);
        Service service = getServiceFactory().create(Echo.class);
        getServiceRegistry().register(service);
        
        Document document = invokeService("Echo", "/org/codehaus/xfire/aegis/type/xml/test.xml");
        addNamespace("e", "urn:PrimitiveService");
        assertValid("//e:echoInt", document);
    }
    
    public static class Echo 
    {
        public Document echo(Document doc)
        {
            return doc;
        }
    }
}
