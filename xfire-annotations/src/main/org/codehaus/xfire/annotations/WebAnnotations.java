package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;

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
     * Gets the {@link org.codehaus.xfire.annotations.WebResultAnnotation} annotation from the given method, if found.
     *
     * @param method the method.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    WebResultAnnotation getWebResultAnnotation(Method method);

    /**
     * Tests whether the indicated parameter of the given method has the {@link WebResultAnnotation} annotation.
     *
     * @param method    the method.
     * @param parameter the index of the parameter in the method's parameter list.
     * @return <code>true> if present; <code>false</code> otherwise.
     */
    boolean hasWebParamAnnotation(Method method, int parameter);

    /**
     * Gets the {@link WebParamAnnotation} annotation from the indicated parameter of the given method, if found.
     *
     * @param method the method.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    WebParamAnnotation getWebParamAnnotation(Method method, int parameter);

    /**
     * Tests whether the given method has the one way annotation.
     *
     * @param method the method.
     * @return <code>true</code> if present; <code>false</code> otherwise.
     */
    boolean hasOnewayAnnotation(Method method);

    /**
     * Tests whether the given class has the {@link SOAPBindingAnnotation} annotation.
     *
     * @param aClass the class.
     * @return <code>true> if present; <code>false</code> otherwise.
     */
    boolean hasSOAPBindingAnnotation(Class aClass);

    /**
     * Gets the {@link SOAPBindingAnnotation} annotation from the given class, if found.
     *
     * @param aClass the class.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    SOAPBindingAnnotation getSOAPBindingAnnotation(Class aClass);

    /**
     * Tests whether the given class has the {@link HandlerChainAnnotation} annotation.
     *
     * @param aClass the class.
     * @return <code>true> if present; <code>false</code> otherwise.
     */
    boolean hasHandlerChainAnnotation(Class aClass);

    /**
     * Gets the {@link HandlerChainAnnotation} annotation from the given class, if found.
     *
     * @param aClass the class.
     * @return the annotation; or <code>null</code> if it could not be found.
     */
    HandlerChainAnnotation getHandlerChainAnnotation(Class aClass);

}
