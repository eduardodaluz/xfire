package org.codehaus.xfire.type.java5;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTypeTest;
import org.codehaus.yom.Document;

public class AnnotatedTypeTest
    extends AbstractXFireTypeTest
{
    public void setUp() throws Exception
    {
        super.setUp();
        
        DefaultObjectService service = (DefaultObjectService)
            getServiceBuilder().create(AnnotatedService.class);
        service.setAutoTyped(false);
        getServiceRegistry().register(service);
        
        AnnotatedType type = new AnnotatedType(AnnotatedBean1.class);
        service.getTypeMapping().register(type);

        type = new AnnotatedType(AnnotatedBean2.class);
        service.getTypeMapping().register(type);
    }

    public void testType()
    {
        AnnotatedTypeInfo type = new AnnotatedTypeInfo(AnnotatedBean1.class);
        
        Iterator elements = type.getElements();
        assertTrue(elements.hasNext());
        QName element = (QName) elements.next();
        assertFalse(elements.hasNext());
        
        Iterator atts = type.getElements();
        assertTrue(atts.hasNext());
        QName att = (QName) atts.next();
        assertFalse(atts.hasNext());
    }
    
    public void testRead()
    {
        
    }
    
    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument("AnnotatedService");
        printNode(wsdl);
        
        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='elementProperty']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:attribute[@name='attributeProperty']", wsdl);
        assertInvalid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='bogusProperty']", wsdl);

        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:sequence/xsd:element[@name='element']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:attribute[@name='attribute']", wsdl);
    }
}