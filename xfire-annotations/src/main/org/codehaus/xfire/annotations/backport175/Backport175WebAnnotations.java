package org.codehaus.xfire.annotations.backport175;

import java.lang.reflect.Method;

import org.codehaus.backport175.reader.Annotations;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebParamAnnotation;
import org.codehaus.xfire.annotations.WebResultAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;
import org.codehaus.xfire.annotations.backport175.soap.SOAPBinding;
import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;

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
        }
        else
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
        }
        else
        {
            return null;
        }
    }

    public boolean hasWebResultAnnotation(Method method)
    {
        return Annotations.isAnnotationPresent(WebResult.class, method);
    }

    public WebResultAnnotation getWebResultAnnotation(Method method)
    {
        WebResult webResult = (WebResult) Annotations.getAnnotation(WebResult.class, method);
        if (webResult != null)
        {
            WebResultAnnotation annotation = new WebResultAnnotation();
            annotation.setName(webResult.name());
            annotation.setTargetNameSpace(webResult.targetNameSpace());
            return annotation;
        }
        else
        {
            return null;
        }
    }

    public boolean hasWebParamAnnotation(Method method, int parameter)
    {
        // Unfortunately, backport175 does not support method parameter annotations as of yet.
        return false;
    }

    public WebParamAnnotation getWebParamAnnotation(Method method, int parameter)
    {
        // Unfortunately, backport175 does not support method parameter annotations as of yet.
        return null;
    }

    public boolean hasOnewayAnnotation(Method method)
    {
        return Annotations.isAnnotationPresent(Oneway.class, method);
    }

    public boolean hasSOAPBindingAnnotation(Class aClass)
    {
        return Annotations.isAnnotationPresent(SOAPBinding.class, aClass);
    }

    public SOAPBindingAnnotation getSoapBindingAnnotation(Class aClass)
    {
        SOAPBinding soapBinding = (SOAPBinding) Annotations.getAnnotation(SOAPBinding.class, aClass);
        if (soapBinding != null)
        {
            SOAPBindingAnnotation annotation = new SOAPBindingAnnotation();
            annotation.setStyle(soapBinding.style());
            annotation.setUse(soapBinding.use());
            annotation.setParameterStyle(soapBinding.parameterStyle());
            return annotation;
        }
        else
        {
            return null;
        }
    }
}
