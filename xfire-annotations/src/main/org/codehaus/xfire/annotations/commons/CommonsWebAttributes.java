package org.codehaus.xfire.annotations.commons;

import java.lang.reflect.Method;

import org.apache.commons.attributes.Attributes;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebResultAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;

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
        return Attributes.hasAttributeType(method, WebResult.class);
    }

    public WebResultAnnotation getWebResultAnnotation(Method method)
    {
        return (WebResultAnnotation) Attributes.getAttribute(method, WebResult.class);
    }
}
