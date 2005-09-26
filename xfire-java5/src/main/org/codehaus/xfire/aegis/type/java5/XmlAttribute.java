package org.codehaus.xfire.aegis.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XmlAttribute
{
    Class type();
    String name() default "";
    String namespace() default "";
}
