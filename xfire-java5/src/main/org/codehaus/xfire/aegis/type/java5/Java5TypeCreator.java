package org.codehaus.xfire.aegis.type.java5;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.AbstractTypeCreator;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.util.NamespaceHelper;

public class Java5TypeCreator
    extends AbstractTypeCreator
{

    @Override
    public TypeClassInfo createClassInfo(Method m, int index)
    {
        if (index >= 0)
        {
            TypeClassInfo info;
            java.lang.reflect.Type genericType = m.getGenericParameterTypes()[index];
            if(genericType instanceof Class)
            {
                info = nextCreator.createClassInfo(m, index);
            }
            else
            {
                info = new TypeClassInfo();
                info.setGenericType(genericType);
            }
            info.setTypeClass(m.getParameterTypes()[index]);
            if(m.getParameterAnnotations()[index].length > 0)
                info.setAnnotations(m.getParameterAnnotations()[index]);
            return info;
        }
        else
        {
            java.lang.reflect.Type genericReturnType = m.getGenericReturnType();
            TypeClassInfo info;
            if(genericReturnType instanceof Class)
            {
                info = nextCreator.createClassInfo(m, index);
            }
            else
            {
                info = new TypeClassInfo();
                info.setGenericType(genericReturnType);
            }
            info.setTypeClass(m.getReturnType());
            if(m.getAnnotations().length > 0)
                info.setAnnotations(m.getAnnotations());
            return info;
        }
    }
    
    @Override
    public TypeClassInfo createClassInfo(PropertyDescriptor pd)
    {
        TypeClassInfo info = createBasicClassInfo(pd.getPropertyType());
        info.setGenericType(pd.getReadMethod().getGenericReturnType());
        info.setAnnotations(pd.getReadMethod().getAnnotations());
        
        return info;
    }

    @Override
    public Type createCollectionType(TypeClassInfo info)
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
    public Type createDefaultType(TypeClassInfo info)
    {
        AnnotatedType type = new AnnotatedType(info.getTypeClass());
        type.setTypeMapping(getTypeMapping());
        
        return type;
    }

    @Override
    public Type createEnumType(TypeClassInfo info)
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
