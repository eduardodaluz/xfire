package org.codehaus.xfire.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XmlType
{
    String type() default "";
    String name() default "";
    String namespace() default "";
}
