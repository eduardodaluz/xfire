package org.codehaus.xfire.type.java5;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.DefaultTypeCreator;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.util.NamespaceHelper;

public class Java5TypeCreator
    extends DefaultTypeCreator
{

    @Override
    protected TypeClassInfo createClassInfo(Method m, int index)
    {
        TypeClassInfo info = new TypeClassInfo();
        if (index >= 0)
        {
            info.setTypeClass(m.getParameterTypes()[index]);
            info.setAnnotations(m.getParameterAnnotations()[index]);
            info.setGenericType(m.getGenericParameterTypes()[index]);
        }
        else
        {
            info.setTypeClass(m.getReturnType());
            info.setAnnotations(m.getAnnotations());
            info.setGenericType(m.getGenericReturnType());
        }
        
        return info;
    }

    @Override
    protected TypeClassInfo createClassInfo(PropertyDescriptor pd)
    {
        TypeClassInfo info = super.createClassInfo(pd);
        
        info.setGenericType(pd.getReadMethod().getGenericReturnType());
        info.setAnnotations(pd.getReadMethod().getAnnotations());
        
        return info;
    }

    @Override
    protected QName createCollectionQName(Class javaType, Class componentType)
    {
        return super.createCollectionQName(javaType, componentType);
    }

    @Override
    protected Type createCollectionType(TypeClassInfo info)
    {
        Object genericType = info.getGenericType();
        Class paramClass = Object.class;
        if (genericType instanceof ParameterizedType)
        {
            ParameterizedType type = (ParameterizedType) genericType;
            
            if (type.getActualTypeArguments()[0] instanceof Class)
            {
                paramClass = (Class) type.getActualTypeArguments()[0];
            }
        }

        return super.createCollectionType(info, paramClass);
    }

    @Override
    protected Type createDefaultType(TypeClassInfo info)
    {
        AnnotatedType type = new AnnotatedType(info.getTypeClass());
        type.setTypeMapping(getTypeMapping());
        
        return type;
    }

    @Override
    protected Type createEnumType(TypeClassInfo info)
    {
        EnumType type = new EnumType();
        
        String name = info.getTypeClass().getSimpleName();
        String ns = NamespaceHelper.makeNamespaceFromClassName(info.getTypeClass().getName(), "http");
        
        type.setSchemaType(new QName(ns, name));
        type.setTypeClass(info.getTypeClass());
        type.setTypeMapping(getTypeMapping());
        
        return type;
    }

    @Override
    protected boolean isEnum(Class javaType)
    {
        return javaType.isEnum();
    }
}
