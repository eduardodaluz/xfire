package org.codehaus.xfire.aegis.type.collection;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class ListTest
        extends AbstractXFireAegisTest
{

    public void setUp()
            throws Exception
    {
        super.setUp();
        Service endpoint = getServiceFactory().create(ListService.class);
        getServiceRegistry().register(endpoint);
    }

    public void testGetStrings()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/GetStrings.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//l:out/l:string[text()='bleh']", response);
    }

    public void testGetDoubles()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/GetDoubles.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//l:out/l:double[text()='1.0']", response);
    }

    public void testReceiveStrings()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveStrings.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//l:receiveStringsResponse", response);
    }

    public void testReceiveDoubles()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveDoubles.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        assertValid("//l:receiveDoublesResponse", response);
    }

    public void testStringsWSDL()
            throws Exception
    {
        Document wsdl = getWSDLDocument("ListService");

        addNamespace("xsd", SoapConstants.XSD);

        String ns = "http://collection.type.aegis.xfire.codehaus.org";
        assertValid("//xsd:schema[@targetNamespace='" + ns + "']/xsd:complexType[@name='ArrayOfString']" +
                    "/xsd:sequence/xsd:element[@name='string'][@type='xsd:string']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='" + ns + "']/xsd:complexType[@name='ArrayOfDouble']" +
                    "/xsd:sequence/xsd:element[@name='double'][@type='xsd:double']", wsdl);
    }
}
