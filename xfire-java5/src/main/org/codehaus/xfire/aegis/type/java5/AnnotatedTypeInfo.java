package org.codehaus.xfire.aegis.type.java5;

import java.beans.PropertyDescriptor;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanTypeInfo;
import org.codehaus.xfire.util.NamespaceHelper;

public class AnnotatedTypeInfo
    extends BeanTypeInfo
{
    public AnnotatedTypeInfo(TypeMapping tm, Class typeClass, String ns)
    {
        super(typeClass, ns);
        setTypeMapping(tm);        
        initialize();
    }
    
    /**
     * Override from parent in order to check for IgnoreProperty annotation.
     */
    protected void mapProperty(PropertyDescriptor pd)
    {
        if (isIgnored(pd))
            return; // do not map ignored properties

        String name = pd.getName();
        if (isAttribute(pd))
        {
            mapAttribute(name, createMappedName(pd));
        }
        else if (isElement(pd))
        {
            mapElement(name, createMappedName(pd));
        }
    }
     
    @Override
    protected boolean registerType(PropertyDescriptor desc)
    {
        XmlAttribute att = desc.getReadMethod().getAnnotation(XmlAttribute.class);
        if (att != null && att.type() != Type.class) return false;
        
        XmlElement el = desc.getReadMethod().getAnnotation(XmlElement.class);
        if (el != null && el.type() != Type.class) return false;
        
        return super.registerType(desc);
    }

    protected boolean isIgnored(PropertyDescriptor desc)
    {
        return desc.getReadMethod().isAnnotationPresent(IgnoreProperty.class);
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
        
    @Override
    protected QName createMappedName(PropertyDescriptor desc)
    {
        return createQName(desc);
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
        PropertyDescriptor desc = getPropertyDescriptorFromMappedName(name);
        
        if (isAnnotatedElement(desc))
        {
            XmlElement att = desc.getReadMethod().getAnnotation(XmlElement.class);
            return att.nillable();
        }
        else
        {
            return super.isNillable(name);
        }
    }
    
    public int getMinOccurs ( QName name )
    {
        PropertyDescriptor desc = getPropertyDescriptorFromMappedName(name);        
        if (isAnnotatedElement(desc))
        {
            XmlElement att = desc.getReadMethod().getAnnotation(XmlElement.class);
            String minOccurs = att.minOccurs();
            if ( minOccurs != null && minOccurs.length() > 0 )
            {
                return Integer.parseInt( minOccurs );
            }
        }
        return super.getMinOccurs(name);
    }
}
