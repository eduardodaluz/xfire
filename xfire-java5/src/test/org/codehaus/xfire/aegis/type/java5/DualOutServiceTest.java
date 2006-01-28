package org.codehaus.xfire.aegis.type.java5;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class DualOutServiceTest
    extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        AnnotationServiceFactory sf = new AnnotationServiceFactory(new Jsr181WebAnnotations(), 
                                                                   getTransportManager());
        
        service = sf.create(DualOutService.class);
        getServiceRegistry().register(service);
    }

    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:element[@name='getValuesResponse']//xsd:element[@name='out'][@type='xsd:string']", wsdl);
        assertValid("//xsd:element[@name='getValuesResponse']//xsd:element[@name='out2'][@type='xsd:string']", wsdl);
    }
}
