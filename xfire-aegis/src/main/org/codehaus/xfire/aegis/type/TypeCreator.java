package org.codehaus.xfire.aegis.type;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface TypeCreator
{
    Type createType(Method m, int index);
    Type createType(PropertyDescriptor pd);
    Type createType(Field f);
    Type createType(Class clazz);
    
    void setTypeMapping(TypeMapping typeMapping);
}
