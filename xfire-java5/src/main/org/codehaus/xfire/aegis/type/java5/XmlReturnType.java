package org.codehaus.xfire.aegis.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.xfire.aegis.type.Type;

/**
 * Annotates a service's return type to provide information about how it is
 * to be serialized.
 * 
 * @author Dan Diephouse
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XmlReturnType
{
    Class type() default Type.class;
    String name() default "";
    String namespace() default "";
}
