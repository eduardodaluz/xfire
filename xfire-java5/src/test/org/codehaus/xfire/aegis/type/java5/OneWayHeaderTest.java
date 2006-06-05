package org.codehaus.xfire.aegis.type.java5;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class OneWayHeaderTest
    extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        AnnotationServiceFactory sf = new AnnotationServiceFactory(new Jsr181WebAnnotations(), 
                                                                   getTransportManager());
        
        service = sf.create(OneWayService.class);
        getServiceRegistry().register(service);
    }

    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:element[@name='foo']", wsdl);
    }
    
    @WebService
    public static class OneWayService
    {
        @WebMethod
        @Oneway
        public void receive(@WebParam(header=true) String foo)
        {
            
        }
    }
}
