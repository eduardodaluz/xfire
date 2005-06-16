package org.codehaus.xfire.aegis.type;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.type.collection.CollectionType;
import org.codehaus.xfire.aegis.type.basic.ArrayType;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;
import org.codehaus.xfire.XFireRuntimeException;

/**
 * @author Hani Suleiman
 *         Date: Jun 14, 2005
 *         Time: 11:59:57 PM
 */
public abstract class AbstractTypeCreator implements TypeCreator
{
    protected TypeMapping tm;
    protected AbstractTypeCreator nextCreator;

    public TypeMapping getTypeMapping()
    {
        return tm;
    }

    public void setTypeMapping(TypeMapping typeMapping)
    {
        this.tm = typeMapping;
    }

    public void setNextCreator(AbstractTypeCreator creator)
    {
        this.nextCreator = creator;
    }

    protected TypeClassInfo createClassInfo(Field f)
    {
        return createBasicClassInfo(f.getType());
    }

    protected TypeClassInfo createBasicClassInfo(Class typeClass)
    {
        TypeClassInfo info = new TypeClassInfo();

        info.setTypeClass(typeClass);

        return info;
    }

    protected Type createTypeForClass(TypeClassInfo info)
    {
        Class javaType = info.getTypeClass();
        if(javaType.isArray())
        {
            return createArrayType(info);
        }
        else if(isCollection(javaType))
        {
            return createCollectionType(info);
        }
        else if(isEnum(javaType))
        {
            return createEnumType(info);
        }
        else
        {
            return createDefaultType(info);
        }
    }

    protected QName createArrayQName(Class javaType)
    {
        return createCollectionQName(javaType, javaType.getComponentType());
    }

    protected Type createArrayType(TypeClassInfo info)
    {
        ArrayType type = new ArrayType();
        type.setSchemaType(createArrayQName(info.getTypeClass()));
        type.setTypeClass(info.getTypeClass());

        return type;
    }

    protected QName createQName(Class javaType)
    {
        String clsName = javaType.getName();

        String ns = NamespaceHelper.makeNamespaceFromClassName(clsName, "http");
        String localName = ServiceUtils.makeServiceNameFromClassName(javaType);

        return new QName(ns, localName);
    }

    protected boolean isCollection(Class javaType)
    {
        return Collection.class.isAssignableFrom(javaType);
    }

    protected Type createCollectionType(TypeClassInfo info, Class component)
    {
        CollectionType type = new CollectionType(component);
        type.setTypeMapping(getTypeMapping());
        type.setSchemaType(createCollectionQName(info.getTypeClass(), component));
        type.setTypeClass(info.getTypeClass());

        return type;
    }

    public abstract TypeClassInfo createClassInfo(PropertyDescriptor pd);

    protected boolean isEnum(Class javaType)
    {
        return false;
    }

    public Type createEnumType(TypeClassInfo info)
    {
        return null;
    }

    public abstract Type createCollectionType(TypeClassInfo info);

    public abstract Type createDefaultType(TypeClassInfo info);

    protected QName createCollectionQName(Class javaType, Class componentType)
    {
        if(componentType == null)
        {
            throw new XFireRuntimeException("Cannot create mapping for " + javaType.getName() + ", unspecified component type");
        }
        Type type = tm.getType(componentType);
        if(type == null)
        {
            type = createType(componentType);
        }
        String ns;

        if(type.isComplex())
        {
            ns = type.getSchemaType().getNamespaceURI();
        }
        else
        {
            ns = tm.getEncodingStyleURI();
        }

        String first = type.getSchemaType().getLocalPart().substring(0, 1);
        String last = type.getSchemaType().getLocalPart().substring(1);
        String localName = "ArrayOf" + first.toUpperCase() + last;

        return new QName(ns, localName);
    }

    public abstract TypeClassInfo createClassInfo(Method m, int index);

    /**
     * Create a Type for a Method parameter.
     *
     * @param m the method to create a type for
     * @param index The parameter index. If the index is less than zero, the return type is used.
     */
    public Type createType(Method m, int index)
    {
        TypeClassInfo info = createClassInfo(m, index);

        return createTypeForClass(info);
    }

    /**
     * Create type information for a PropertyDescriptor.
     *
     * @param pd the propertydescriptor
     */
    public Type createType(PropertyDescriptor pd)
    {
        TypeClassInfo info = createClassInfo(pd);

        return createTypeForClass(info);
    }

    /**
     * Create type information for a <code>Field</code>.
     *
     * @param f the field to create a type from
     */
    public Type createType(Field f)
    {
        TypeClassInfo info = createClassInfo(f);

        return createTypeForClass(info);
    }

    public Type createType(Class clazz)
    {
        TypeClassInfo info = createBasicClassInfo(clazz);

        return createTypeForClass(info);
    }

    public static class TypeClassInfo
    {
        Class typeClass;
        Object[] annotations;
        Object genericType;

        public Object[] getAnnotations()
        {
            return annotations;
        }

        public void setAnnotations(Object[] annotations)
        {
            this.annotations = annotations;
        }

        public Object getGenericType()
        {
            return genericType;
        }

        public void setGenericType(Object genericType)
        {
            this.genericType = genericType;
        }

        public Class getTypeClass()
        {
            return typeClass;
        }

        public void setTypeClass(Class typeClass)
        {
            this.typeClass = typeClass;
        }
    }
}
