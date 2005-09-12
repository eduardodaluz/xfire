package org.codehaus.xfire.aegis.type.java5;

import java.beans.PropertyDescriptor;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanTypeInfo;
import org.codehaus.xfire.util.NamespaceHelper;

public class AnnotatedTypeInfo
    extends BeanTypeInfo
{
    public AnnotatedTypeInfo(TypeMapping tm, Class typeClass)
    {
        super(typeClass);
        setTypeMapping(tm);
        
        initialize();
    }

    protected boolean isAttribute(PropertyDescriptor desc)
    {
        return desc.getReadMethod().isAnnotationPresent(XmlAttribute.class);
    }

    protected boolean isElement(PropertyDescriptor desc)
    {
        return !isAttribute(desc);
    }

    protected boolean isAnnotatedElement(PropertyDescriptor desc)
    {
        return desc.getReadMethod().isAnnotationPresent(XmlElement.class);
    }
    
    protected QName createQName(PropertyDescriptor desc)
    {
        String name = null;
        String ns = null;
        
        XmlType xtype = (XmlType) getTypeClass().getAnnotation(XmlType.class);
        if (xtype != null)
        {
            ns = xtype.namespace();
        }

        if (isAttribute(desc))
        {
            XmlAttribute att = desc.getReadMethod().getAnnotation(XmlAttribute.class);
            name = att.name();
            if (att.namespace().length() > 0) ns = att.namespace();
        }
        else if (isAnnotatedElement(desc))
        {
            XmlElement att = desc.getReadMethod().getAnnotation(XmlElement.class);
            name = att.name();
            if (att.namespace().length() > 0) ns = att.namespace();
        }
        
        if (name == null || name.length() == 0)
            name = desc.getName();
        
        if (ns == null || ns.length() == 0)
            ns = NamespaceHelper.makeNamespaceFromClassName( getTypeClass().getName(), "http");
        
        return new QName(ns, name);
    }

    public boolean isNillable(QName name)
    {
        return super.isNillable(name);
    }
}
