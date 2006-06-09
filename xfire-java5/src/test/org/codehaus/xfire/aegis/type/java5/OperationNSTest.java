package org.codehaus.xfire.aegis.type.java5;

import java.util.List;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class OperationNSTest
    extends AbstractXFireAegisTest
{
    private Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        AnnotationServiceFactory sf = new AnnotationServiceFactory(new Jsr181WebAnnotations(), 
                                                                   getTransportManager());
        sf.setStyle("document");
        service = sf.create(NotificationLogImpl.class);
        getServiceRegistry().register(service);
    }

    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument(service.getSimpleName());

        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:element[@name='Notify']", wsdl);
    }
    
    @WebService(name = "NotificationLog", targetNamespace =
    "http://www.sics.se/NotificationLog")
    public static interface NotificationLog {

       @WebMethod(operationName = "Notify", action = "")
       @Oneway
       public void Notify(
           @WebParam(name = "Notify",  targetNamespace =
    "http://docs.oasis-open.org/wsn/b-2")
           Document Notify);

       @WebMethod(operationName = "query", action = "")
       @WebResult(name = "queryResponse", targetNamespace =
    "http://www.sics.se/NotificationLog")
       public List<Document> query(
           @WebParam(name = "xpath", targetNamespace =
    "http://www.sics.se/NotificationLog")
           String xpath);

       @WebMethod(operationName = "Notify2", action = "")
       @Oneway
       public void Notify2(
           @WebParam(name = "Notify", targetNamespace =
    "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd")
           Document Notify);
    } 
    @WebService(endpointInterface="org.codehaus.xfire.aegis.type.java5.OperationNSTest$NotificationLog")
    public static class NotificationLogImpl implements NotificationLog {

        public void Notify(Document Notify)
        {
        }

        public void Notify2(Document Notify)
        {
        }

        public List<Document> query(String xpath)
        {
            return null;
        }
    }
}
