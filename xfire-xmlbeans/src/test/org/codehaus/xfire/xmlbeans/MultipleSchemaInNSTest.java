package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * Tests that we can handle multiple schemas within the same namespace.
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MultipleSchemaInNSTest
        extends AbstractXmlBeansTest
{
    private Service endpoint;
    String ns = "urn:xfire:xmlbeans:nstest";

    public void setUp()
            throws Exception
    {
        super.setUp();

        endpoint = getServiceFactory().create(MultipleSchemaService.class,
                                              null,
                                              ns,
                                              null);
                    
        getServiceRegistry().register(endpoint);
    }

    /*
    public void testInvoke() throws Exception
    {
        Document response = invokeService("TestService", 
                                          "/org/codehaus/xfire/xmlbeans/DocumentStyleRequest.xml");
        
        assertNotNull(response);

        addNamespace("x", "http://codehaus.org/xfire/xmlbeans");
        assertValid("//s:Body/x:response/x:form", response);
    }*/
    
    public void testWSDL() throws Exception
    {
//        Document wsdl = getWSDLDocument("MultipleSchemaService");
//
//        addNamespace("xsd", SoapConstants.XSD);
//        assertValid("//xsd:schema[@targetNamespace='" + ns + "']", wsdl);
    }
}
