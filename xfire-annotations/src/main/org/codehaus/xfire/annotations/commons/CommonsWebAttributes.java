package org.codehaus.xfire.annotations.commons;

import java.lang.reflect.Method;

import org.apache.commons.attributes.Attributes;
import org.codehaus.xfire.annotations.*;

/**
 * Implementation of the {@link WebAnnotations} facade for Commons Attributes.
 *
 * @author Arjen Poutsma
 */
public class CommonsWebAttributes
        implements WebAnnotations
{

    public boolean hasWebServiceAnnotation(Class aClass)
    {
        return Attributes.hasAttributeType(aClass, WebService.class);
    }

    public WebServiceAnnotation getWebServiceAnnotation(Class aClass)
    {
        return (WebServiceAnnotation) Attributes.getAttribute(aClass, WebService.class);
    }

    public boolean hasWebMethodAnnotation(Method method)
    {
        return Attributes.hasAttributeType(method, WebMethod.class);
    }

    public WebMethodAnnotation getWebMethodAnnotation(Method method)
    {
        return (WebMethodAnnotation) Attributes.getAttribute(method, WebMethod.class);
    }

    public boolean hasWebResultAnnotation(Method method)
    {
        return Attributes.hasReturnAttributeType(method, WebResult.class);
    }

    public WebResultAnnotation getWebResultAnnotation(Method method)
    {
        return (WebResultAnnotation) Attributes.getReturnAttribute(method, WebResult.class);
    }

    public boolean hasWebParamAnnotation(Method method, int parameter)
    {
        return Attributes.hasParameterAttributeType(method, parameter, WebParam.class);
    }

    public WebParamAnnotation getWebParamAnnotation(Method method, int parameter)
    {
        return (WebParamAnnotation) Attributes.getParameterAttribute(method, parameter, WebParam.class);
    }
}
