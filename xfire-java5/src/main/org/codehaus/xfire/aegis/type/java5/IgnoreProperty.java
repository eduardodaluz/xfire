/**
 * 
 */
package org.codehaus.xfire.aegis.type.java5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IgnoreProperty Annotation allows us to Ignore JavaBean Properties
 * when using AEGIS POJO Binding with Java 5 Annotations
 * 
 * @author <a href="mailto:adam@multicom.co.uk">Adam J Chesney</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreProperty
{

}
