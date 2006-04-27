package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.binding.ServiceConfiguration;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Annotations-based implementation of the {@link ServiceConfiguration} class.
 *
 * @author Dan Diephouse
 * @author Arjen Poutsma
 */
public class AnnotationServiceConfiguration
        extends ServiceConfiguration
{
    private WebAnnotations webAnnotations;

    public void setServiceFactory(ObjectServiceFactory serviceFactory)
    {
    }

    public void setWebAnnotations(WebAnnotations webAnnotations)
    {
        this.webAnnotations = webAnnotations;
    }

    public String getOperationName(ServiceInfo service, Method method)
    {
    	if (webAnnotations.hasWebMethodAnnotation(method)) 
    	{
            WebMethodAnnotation wma = webAnnotations.getWebMethodAnnotation(method);
            if (wma.getOperationName().length() > 0)
                return wma.getOperationName();
    	}
        
        return super.getOperationName(service, method);
    }
    
    public String getAction(OperationInfo op)
    {
        if (webAnnotations.hasWebMethodAnnotation(op.getMethod()))
        {
            WebMethodAnnotation wma = webAnnotations.getWebMethodAnnotation(op.getMethod());
            if (wma.getAction().length() > 0)
                return wma.getAction();
        }
        
        return super.getAction(op);
    }

    /**
     * Returns <code>true</code> if the specified method is valid for a SOAP operation.
     *
     * @param method the method.
     * @return <code>true</code> if valid; <code>false</code> otherwise.
     */
    public Boolean isOperation(Method method)
    {
        if (webAnnotations.hasWebMethodAnnotation(method))
        {
            WebMethodAnnotation ann = webAnnotations.getWebMethodAnnotation(method);
            if (ann.isExclude()) return Boolean.FALSE;
        }
        
        // All methods on endpoint interfaces are valid WebMethods.
        if (method.getDeclaringClass().isInterface())
            return Boolean.TRUE;

        return super.isOperation(method);
    }

    public Boolean isHeader(Method method, int paramNumber)
    {
        if (paramNumber != -1)
        {
            if (webAnnotations.hasWebParamAnnotation(method, paramNumber))
            {
                final WebParamAnnotation webParamAnnotation = 
                    webAnnotations.getWebParamAnnotation(method, paramNumber);
                
                return new Boolean(webParamAnnotation.isHeader());
            }
        }
        else
        {
            if (webAnnotations.hasWebResultAnnotation(method))
            {
                final WebResultAnnotation ann = 
                    webAnnotations.getWebResultAnnotation(method);
                
                return new Boolean(ann.isHeader());
            }
        }
        
        return super.isHeader(method, paramNumber);
    }
    
    public Boolean isInParam(Method method, int j)
    {
        if (webAnnotations.hasWebParamAnnotation(method, j))
        {
            final WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, j);

            return new Boolean(webParamAnnotation.getMode() == WebParamAnnotation.MODE_IN ||
                webParamAnnotation.getMode() == WebParamAnnotation.MODE_INOUT);
        }
        
        return super.isInParam(method, j);
    }
    
    public Boolean isOutParam(Method method, int j)
    {
        if (webAnnotations.hasWebParamAnnotation(method, j))
        {
            final WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, j);

            return new Boolean(webParamAnnotation.getMode() == WebParamAnnotation.MODE_OUT ||
                webParamAnnotation.getMode() == WebParamAnnotation.MODE_INOUT);
        }
        else
        {
            return super.isOutParam(method, j);
        }
    }

    public QName getInParameterName(Service endpoint, OperationInfo op, Method method, int paramNumber, boolean doc)
    {
        if (webAnnotations.hasWebParamAnnotation(method, paramNumber))
        {
            final WebParamAnnotation webParamAnnotation = webAnnotations.getWebParamAnnotation(method, paramNumber);

            String name = webParamAnnotation.getName();
            if (name == null || name.length() == 0)
            {
                name = super.getInParameterName(endpoint, op, method, paramNumber, doc).getLocalPart();
            }
            
            String ns = webParamAnnotation.getTargetNamespace();

            if (ns == null || ns.length() == 0) 
            {
                ns = endpoint.getTargetNamespace();
            }

            return new QName(ns, name);
        }
        else
        {
            return super.getInParameterName(endpoint, op, method, paramNumber, doc);
        }
    }

    public QName getOutParameterName(Service endpoint, OperationInfo op, Method method, boolean doc)
    {
        if (webAnnotations.hasWebResultAnnotation(method))
        {
            final WebResultAnnotation webResultAnnotation = webAnnotations.getWebResultAnnotation(method);

            String name = webResultAnnotation.getName();
            if (name == null || name.length() == 0)
            {
                name = super.getOutParameterName(endpoint, op, method, doc).getLocalPart();
            }
            
            String ns = webResultAnnotation.getTargetNamespace();

            if (ns == null || ns.length() == 0)
            {
                ns = endpoint.getTargetNamespace();
            }

            return new QName(ns, name);
        }
        else
        {
            return super.getOutParameterName(endpoint, op, method, doc);
        }
    }

    public Boolean isAsync(Method method)
    {
        boolean oneway = webAnnotations.hasOnewayAnnotation(method);
        
        if (oneway && !method.getReturnType().equals(void.class))
        {
            throw new AnnotationException("Method " + method.getName() + 
                                          " is marked as Oneway, but does not return void!");
        }

        if (oneway) return Boolean.TRUE;
        
        return super.isAsync(method);
    }

    public String getMEP(Method method)
    {
        if (webAnnotations.hasOnewayAnnotation(method))
        {
            return SoapConstants.MEP_IN;
        }
        
        return super.getMEP(method);
    }
}
