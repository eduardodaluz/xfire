package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DocumentStyleTest
        extends AbstractXmlBeansTest
{
    private Service endpoint;

    public void setUp()
            throws Exception
    {
        super.setUp();

        endpoint = getServiceFactory().create(TestService.class,
                                              "TestService",
                                              "urn:TestService",
                                              null);
                    
        getServiceRegistry().register(endpoint);
    }

    public void testInvoke() throws Exception
    {
        Document response = invokeService("TestService", 
                                          "/org/codehaus/xfire/xmlbeans/DocumentStyleRequest.xml");
        
        assertNotNull(response);

        addNamespace("x", "http://codehaus.org/xfire/xmlbeans");
        assertValid("//s:Body/x:response/x:form", response);
    }
    
    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument("TestService");
        printNode(wsdl);
        
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:schema[@targetNamespace='urn:TestService']" +
                "/xsd:element[@name='mixedRequestin0'][@type='xsd:string']", wsdl);
    }
}
