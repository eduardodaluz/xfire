package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

/**
 * Defines the contract for accessing annotations at runtime. This is a facade,  which can accommodate any annotations
 * API such as Commons Attributes,  backport175, Java 5, or any other annotations implementation.
 *
 * @author Arjen Poutsma
 */
public interface WebAnnotations
{
    /**
     * Tests whether the given class has the {@link org.codehaus.xfire.annotations.WebServiceAnnotation} annotation.
     *
     * @param aClass the class.
     * @return <code>true</code> if present; <code>false</code> otherwise.
     */
    boolean hasWebServiceAnnotation(Class aClass);

    /**
     * Gets the {@link org.codehaus.xfire.annotations.WebServiceAnnotation} annotation from the given class, if found.
     *
     * @param aClass the class.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    WebServiceAnnotation getWebServiceAnnotation(Class aClass);

    /**
     * Tests whether the given method has the {@link org.codehaus.xfire.annotations.WebMethodAnnotation} annotation.
     *
     * @param method the method.
     * @return <code>true</code> if present; <code>false</code> otherwise.
     */
    boolean hasWebMethodAnnotation(Method method);

    /**
     * Gets the {@link org.codehaus.xfire.annotations.WebServiceAnnotation} annotation from the given class, if found.
     *
     * @param method the method.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    WebMethodAnnotation getWebMethodAnnotation(Method method);

    /**
     * Tests whether the given method has the {@link org.codehaus.xfire.annotations.WebResultAnnotation} annotation.
     *
     * @param method the method.
     * @return <code>true</code> if present; <code>false</code> otherwise.
     */
    boolean hasWebResultAnnotation(Method method);

    /**
     * Gets the {@link org.codehaus.xfire.annotations.WebResultAnnotation} annotation from the given class, if found.
     *
     * @param method the method.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    WebResultAnnotation getWebResultAnnotation(Method method);
}