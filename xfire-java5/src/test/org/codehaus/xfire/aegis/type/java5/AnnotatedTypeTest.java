package org.codehaus.xfire.aegis.type.java5;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class AnnotatedTypeTest
    extends AbstractXFireAegisTest
{
    private TypeMapping tm;
    private Service service;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        ObjectServiceFactory osf = (ObjectServiceFactory) getServiceFactory();
        service = osf.create(AnnotatedService.class);

        getServiceRegistry().register(service);
        tm = ((AegisBindingProvider) osf.getBindingProvider()).getTypeMapping(service);
    }

    public void testTM()
    {
        assertTrue( tm.getTypeCreator() instanceof Java5TypeCreator );
    }
    
    public void testType()
    {
        AnnotatedTypeInfo info = new AnnotatedTypeInfo(tm, AnnotatedBean1.class);
        
        Iterator elements = info.getElements();
        assertTrue(elements.hasNext());
        String element = (String) elements.next();
        assertTrue(elements.hasNext());
        
        element = (String) elements.next();
        assertFalse(elements.hasNext());
        
        Type custom = info.getType(element);
        // TODO: Fix custom types
        // assertTrue(custom instanceof CustomStringType);
        
        Iterator atts = info.getAttributes();
        assertTrue(atts.hasNext());
        String att = (String) atts.next();
        assertFalse(atts.hasNext());
    }

    public void testWSDL() throws Exception
    {
        Document wsdl = getWSDLDocument("AnnotatedService");

        addNamespace("xsd", SoapConstants.XSD);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='elementProperty']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:attribute[@name='attributeProperty']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean1']/xsd:sequence/xsd:element[@name='bogusProperty']", wsdl);

        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:sequence/xsd:element[@name='element'][@type='xsd:string']", wsdl);
        assertValid("//xsd:complexType[@name='AnnotatedBean2']/xsd:attribute[@name='attribute'][@type='xsd:string']", wsdl);
    }
    
    public void testGetSetRequired() throws Exception
    {
        BeanType type = new BeanType(new AnnotatedTypeInfo(tm, BadBean.class));
        type.setSchemaType(new QName("urn:foo", "BadBean"));
        
        assertFalse(type.getTypeInfo().getElements().hasNext());
        
        type = new BeanType(new AnnotatedTypeInfo(tm, BadBean2.class));
        type.setTypeClass(BadBean2.class);
        type.setSchemaType(new QName("urn:foo", "BadBean2"));
        
        assertFalse(type.getTypeInfo().getElements().hasNext());
    }
    
    // This class only has a read property, no write
    public static class BadBean
    {
        private String string;

        public String getString()
        {
            return string;
        }
    }
    
    public static class BadBean2
    {
        private String string;

        public void setString(String string)
        {
            this.string = string;
        }
    }
}
