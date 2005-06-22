package org.codehaus.xfire.type.java5;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;

public class AnnotatedType
    extends BeanType
{
    public AnnotatedType(Class clazz)
    {
        super(new AnnotatedTypeInfo(clazz));
    }

    public QName getSchemaType()
    {
        QName schemaType = super.getSchemaType();
        
        if (schemaType == null)
        {
            String name = null;
            String ns = null;
            
            String clsName = getTypeClass().getName();
            if (name == null || name.length() == 0)
                name = ServiceUtils.makeServiceNameFromClassName(getTypeClass());
            
            if (ns == null || ns.length() == 0)
                ns = NamespaceHelper.makeNamespaceFromClassName(clsName, "http");
            
            schemaType = new QName(ns, name);
            setSchemaType(schemaType);
        }
        
        return schemaType;
    }

    @Override
    public void setTypeMapping(TypeMapping typeMapping)
    {
        getTypeInfo().setTypeMapping(typeMapping);
        
        super.setTypeMapping(typeMapping);
    }
    
    
}
