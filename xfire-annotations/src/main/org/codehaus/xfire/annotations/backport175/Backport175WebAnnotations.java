package org.codehaus.xfire.annotations.backport175;

import java.lang.reflect.Method;

import org.codehaus.backport175.reader.Annotations;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;

/**
 * Implementation of the {@link WebAnnotations} facade for backport175.
 *
 * @author Arjen Poutsma
 */
public class Backport175WebAnnotations
        implements WebAnnotations
{
    public boolean hasWebServiceAnnotation(Class aClass)
    {
        return Annotations.isAnnotationPresent(WebService.class, aClass);
    }

    public WebServiceAnnotation getWebServiceAnnotation(Class aClass)
    {
        WebService webService = (WebService) Annotations.getAnnotation(WebService.class, aClass);
        if (webService != null)
        {
            WebServiceAnnotation annotation = new WebServiceAnnotation();
            annotation.setEndpointInterface(webService.endpointInterface());
            annotation.setName(webService.name());
            annotation.setServiceName(webService.serviceName());
            annotation.setTargetNamespace(webService.targetNamespace());
            return annotation;
        } else
        {
            return null;
        }
    }

    public boolean hasWebMethodAnnotation(Method method)
    {
        return Annotations.isAnnotationPresent(WebMethod.class, method);
    }

    public WebMethodAnnotation getWebMethodAnnotation(Method method)
    {
        WebMethod webMethod = (WebMethod) Annotations.getAnnotation(WebMethod.class, method);
        if (webMethod != null)
        {
            WebMethodAnnotation annotation = new WebMethodAnnotation();
            annotation.setAction(webMethod.action());
            annotation.setOperationName(webMethod.operationName());
            return annotation;
        } else
        {
            return null;
        }
    }
}
