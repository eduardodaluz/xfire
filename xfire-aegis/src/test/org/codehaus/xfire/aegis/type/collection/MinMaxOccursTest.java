package org.codehaus.xfire.aegis.type.collection;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.jdom.Document;

public class MinMaxOccursTest
        extends AbstractXFireAegisTest
{

    private Service service;

    public void setUp()
            throws Exception
    {
        super.setUp();
        service = getServiceFactory().create(ListService.class, null, "urn:min-max-test", null);
        getServiceRegistry().register(service);
    }

    public void testReceiveStrings()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveTooManyStrings.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//s:Fault", response);
        
        response = invokeService("ListService",
                "/org/codehaus/xfire/aegis/type/collection/ReceiveTooFewStrings.xml");
        
        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//s:Fault", response);
    }
/*
    public void testReceiveDoubles()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveDoubles.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//l:receiveDoublesResponse", response);
    }*/

    /*public void testStringsWSDL()
            throws Exception
    {
        Document wsdl = getWSDLDocument("ListService");

        addNamespace("xsd", SoapConstants.XSD);

        String ns = "urn:min-max-test";
        assertValid("//xsd:schema[@targetNamespace='" + ns + "']/xsd:complexType[@name='ArrayOfString']" +
                    "/xsd:sequence/xsd:element[@name='string'][@type='xsd:string'][@minOccurs='1']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='" + ns + "']/xsd:complexType[@name='ArrayOfDouble']" +
                    "/xsd:sequence/xsd:element[@name='double'][@type='xsd:double']", wsdl);
    }*/
}
