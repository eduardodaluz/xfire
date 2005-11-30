package org.codehaus.xfire.aegis.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.xfire.aegis.type.Type;

/**
 * Annotates services method parameters to provide information about how they are
 * to be serialized.
 * 
 * @author Dan Diephouse
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XmlParamType
{
    Class type() default Type.class;
    String name() default "";
    String namespace() default "";
}
