package org.codehaus.xfire.aegis.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.xfire.aegis.type.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XmlElement
{
    Class type() default Type.class;
    String name() default "";
    String namespace() default "";
    boolean nillable() default true;
    
    /**
     * ajc - set to "0" to make the property optional, "1" for required
     */
    String minOccurs() default "";
}
