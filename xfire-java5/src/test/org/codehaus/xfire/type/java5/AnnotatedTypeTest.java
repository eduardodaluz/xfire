package org.codehaus.xfire.type.java5;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.yom.Document;

public class AnnotatedTypeTest
    extends AbstractXFireAegisTest
{
    private TypeMapping tm;
    private Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(AnnotatedService.class);

        getServiceRegistry().register(service);
        tm = AegisBindingProvider.getTypeMapping(service);
    }

    public void testTM()
    {
        assertTrue( tm.getTypeCreator() instanceof Java5TypeCreator );
    }
    
    public void testType()
    {
        AnnotatedTypeInfo type = new AnnotatedTypeInfo(AnnotatedBean1.class);
        type.setTypeMapping(tm);
        
        Iterator elements = type.getElements();
        assertTrue(elements.hasNext());
        QName element = (QName) elements.next();
        assertTrue(elements.hasNext());
        element = (QName) elements.next();
        assertFalse(elements.hasNext());
        
        Iterator atts = type.getAttributes();
        assertTrue(atts.hasNext());
        QName att = (QName) atts.next();
        assertFalse(atts.hasNext());
    }

    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument("AnnotatedService");
        printNode(wsdl);
        
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='elementProperty']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:attribute[@name='attributeProperty']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='bogusProperty']", wsdl);

        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:sequence/xsd:element[@name='element']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:attribute[@name='attribute']", wsdl);
    }
}
