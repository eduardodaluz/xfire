package org.codehaus.xfire.aegis.type.collection;

import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.collection.CollectionType;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
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

        TypeMapping tm = (TypeMapping) endpoint.getProperty(AegisBindingProvider.TYPE_MAPPING_KEY);
        CollectionType strings = new CollectionType(String.class);
        QName strQ = new QName("urn:string", "strings");
        strings.setSchemaType(strQ);
        strings.setTypeClass(List.class);
        tm.register(strings);

        CollectionType doubles = new CollectionType(Double.class);
        QName dblQ = new QName("urn:double", "doubles");
        doubles.setSchemaType(dblQ);
        doubles.setTypeClass(List.class);
        tm.register(doubles);

        ServiceInfo service = endpoint.getServiceInfo();
        OperationInfo o = service.getOperation("getDoubles");
        MessageInfo outMsg = o.getOutputMessage();
        MessagePartInfo p = outMsg.getMessagePart(new QName(service.getName().getNamespaceURI(), "out"));
        p.setSchemaType(doubles);

        o = service.getOperation("getStrings");
        outMsg = o.getOutputMessage();
        p = outMsg.getMessagePart(new QName(service.getName().getNamespaceURI(), "out"));
        p.setSchemaType(strings);

        o = service.getOperation("receiveDoubles");
        p = o.getInputMessage().getMessagePart(new QName(service.getName().getNamespaceURI(), "in0"));
        p.setSchemaType(doubles);

        o = service.getOperation("receiveStrings");
        p = o.getInputMessage().getMessagePart(new QName(service.getName().getNamespaceURI(), "in0"));
        p.setSchemaType(strings);
    }

    public void testGetStrings()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/GetStrings.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        addNamespace("s", "urn:string");
        assertValid("//l:out/s:string[text()='bleh']", response);
    }

    public void testGetDoubles()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/GetDoubles.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        addNamespace("s", "urn:double");
        assertValid("//l:out/s:double[text()='1.0']", response);
    }

    public void testReceiveStrings()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveStrings.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        addNamespace("s", "urn:string");
        assertValid("//l:receiveStringsResponse", response);
    }

    public void testReceiveDoubles()
            throws Exception
    {
        Document response = invokeService("ListService",
                                          "/org/codehaus/xfire/aegis/type/collection/ReceiveDoubles.xml");

        addNamespace("l", "http://collection.type.aegis.xfire.codehaus.org");
        addNamespace("s", "urn:double");
        assertValid("//l:receiveDoublesResponse", response);
    }

    public void testStringsWSDL()
            throws Exception
    {
        Document wsdl = getWSDLDocument("ListService");

        addNamespace("xsd", SoapConstants.XSD);

        assertValid("//xsd:schema[@targetNamespace='urn:string']/xsd:complexType[@name='strings']" +
                    "/xsd:sequence/xsd:element[@name='string'][@type='xsd:string']", wsdl);
        assertValid("//xsd:schema[@targetNamespace='urn:double']/xsd:complexType[@name='doubles']" +
                    "/xsd:sequence/xsd:element[@name='double'][@type='xsd:double']", wsdl);
        assertValid("//xsd:element[@name='getStringsResponse']/xsd:complexType/xsd:sequence" +
                    "/xsd:element[@name='out'][@type='ns1:strings']", wsdl);
        assertValid("//xsd:element[@name='getDoublesResponse']/xsd:complexType/xsd:sequence" +
                    "/xsd:element[@name='out'][@type='ns2:doubles']", wsdl);
    }
}
