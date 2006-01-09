package org.codehaus.xfire.xmlbeans;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class IDRefServiceTest
        extends AbstractXmlBeansTest
{
    private Service endpoint;
    private ObjectServiceFactory factory;

    public void setUp()
            throws Exception
    {
        super.setUp();

        factory = (ObjectServiceFactory) getServiceFactory();
        factory.setStyle(SoapConstants.STYLE_WRAPPED);
        
        endpoint = factory.create(IDRefService.class, null, "urn:xfire:idref", null);
        
        getServiceRegistry().register(endpoint);
    }

    public void testInvoke() throws Exception
    {
        Document response = getWSDLDocument("IDRefService");
        
        /*
        assertNotNull(response);

        SampleElementDocument doc = SampleElementDocument.Factory.newInstance();
        SampleElement sampleElement = doc.addNewSampleElement();
        SampleUserInformation information = sampleElement.addNewSampleUserInformation();
        
        addNamespace("t", "urn:TestService");
        addNamespace("x", "http://codehaus.org/xfire/xmlbeans");
        assertValid("//t:mixedRequestResponse/x:response/x:form", response);*/
    }
}
