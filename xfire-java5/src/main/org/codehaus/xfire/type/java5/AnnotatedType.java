package org.codehaus.xfire.type.java5;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.util.NamespaceHelper;

public class AnnotatedType
    extends BeanType
{
    private QName schemaType;
    
    public AnnotatedType(Class clazz)
    {
        super(new AnnotatedTypeInfo(clazz));
    }

    public QName getSchemaType()
    {
        if (schemaType == null)
        {
            String name = null;
            String ns = null;
            
            String clsName = getTypeClass().getName();
            if (name == null || name.length() == 0)
                name = clsName.substring(clsName.lastIndexOf(".")+1);
            
            if (ns == null || ns.length() == 0)
                ns = NamespaceHelper.makeNamespaceFromClassName(clsName, "http");
            
            schemaType = new QName(ns, name);
        }
        return schemaType;
    }
    
    
}
