package org.codehaus.xfire.aegis.type.basic;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.AutoTypeMapping;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.aegis.type.basic.TypeInfo;

public class DescriptorTest
    extends AbstractXFireAegisTest
{
    public void testMapping() throws Exception
    {
        TypeMapping tm = new AutoTypeMapping();
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
        TypeMapping tm = new AutoTypeMapping();
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

}
