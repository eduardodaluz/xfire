package org.codehaus.xfire.aegis.type.basic;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.collection.CollectionType;

public class DescriptorTest
    extends AbstractXFireAegisTest
{
    CustomTypeMapping tm;
    
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        tm = new CustomTypeMapping();
        tm.setTypeCreator(new DefaultTypeCreator());
    }

    public void testMapping() throws Exception
    {
        tm.setEncodingStyleURI("urn:xfire:bean");

        Type type = tm.getType(MyBean.class);
        TypeInfo info = ((BeanType) type).getTypeInfo();
        
        Iterator elItr = info.getElements();
        assertTrue(elItr.hasNext());
        QName el = (QName) elItr.next();
        assertEquals("urn:xfire:bean", el.getNamespaceURI());
        assertEquals("Prop1", el.getLocalPart());
        
        Iterator attItr = info.getAttributes();
        assertTrue(attItr.hasNext());
        QName att = (QName) attItr.next();
        assertEquals("urn:xfire:bean", att.getNamespaceURI());
        assertEquals("Prop2", att.getLocalPart());
    }
    
    public void testMapping2() throws Exception
    {
        tm.setEncodingStyleURI("urn:xfire:bean2");

        Type type = tm.getType(MyBean.class);
        TypeInfo info = ((BeanType) type).getTypeInfo();
        
        Iterator elItr = info.getElements();
        assertTrue(elItr.hasNext());
        QName el = (QName) elItr.next();
        assertEquals("urn:xfire:bean2", el.getNamespaceURI());
        assertEquals("Prop1", el.getLocalPart());
        
        assertTrue(elItr.hasNext());
        QName el2 = (QName) elItr.next();
        assertEquals("urn:xfire:bean2", el2.getNamespaceURI());
        assertEquals("Prop2", el2.getLocalPart());
    }

    public void testListHolder() throws Exception
    {
        tm.setEncodingStyleURI("urn:xfire:bean");

        Type type = tm.getType(ListHolderBean.class);
        TypeInfo info = ((BeanType) type).getTypeInfo();
        
        Iterator elItr = info.getElements();
        assertTrue(elItr.hasNext());
        QName el = (QName) elItr.next();
        assertEquals("urn:xfire:bean", el.getNamespaceURI());
        assertEquals("Beans", el.getLocalPart());
        
        Type beanList = info.getType(el);
        assertTrue( beanList instanceof CollectionType );
    }

    public void testListHolderNoName() throws Exception
    {
        tm.setEncodingStyleURI("urn:xfire:bean2");

        Type type = tm.getType(ListHolderBean.class);
        TypeInfo info = ((BeanType) type).getTypeInfo();
        
        Iterator elItr = info.getElements();
        assertTrue(elItr.hasNext());
        QName el = (QName) elItr.next();
        assertEquals("urn:xfire:bean2", el.getNamespaceURI());
        assertEquals("beans", el.getLocalPart());
        
        Type beanList = info.getType(el);
        assertTrue( beanList instanceof CollectionType );
    }
    
    public void testDefaultName() throws Exception
    {
        tm.setEncodingStyleURI("urn:xfire:bean4");

        Type type = tm.getType(MyBean.class);
        TypeInfo info = ((BeanType) type).getTypeInfo();
        
        Iterator attItr = info.getAttributes();
        assertTrue(attItr.hasNext());
        QName el = (QName) attItr.next();
        assertEquals("urn:xfire:bean4", el.getNamespaceURI());
        assertEquals("prop2", el.getLocalPart());
    }   
}
