package org.codehaus.xfire.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XmlElement
{
    String type() default "";
    String name() default "";
    String namespace() default "";
    boolean isNillable() default true;
}
