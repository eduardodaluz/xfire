package org.codehaus.xfire.annotations.jsr181;

import java.lang.reflect.Method;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebParamAnnotation;
import org.codehaus.xfire.annotations.WebResultAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;

public class JSR181Annotations
    implements WebAnnotations
{
    public boolean hasWebServiceAnnotation(Class clazz)
    {
        return clazz.isAnnotationPresent(WebService.class);
    }

    public WebServiceAnnotation getWebServiceAnnotation(Class clazz)
    {
        WebService ws = (WebService) clazz.getAnnotation(WebService.class);
        
        WebServiceAnnotation annot = new WebServiceAnnotation();
        annot.setEndpointInterface(ws.endpointInterface());
        annot.setName(ws.name());
        annot.setServiceName(ws.serviceName());
        annot.setTargetNamespace(ws.targetNamespace());
        
        return annot;
    }

    public boolean hasWebMethodAnnotation(Method method)
    {
        return method.isAnnotationPresent(WebMethod.class);
    }

    public WebMethodAnnotation getWebMethodAnnotation(Method method)
    {
        WebMethod ws = (WebMethod) method.getAnnotation(WebMethod.class);
        
        WebMethodAnnotation annot = new WebMethodAnnotation();
        annot.setAction(ws.operationName());
        annot.setOperationName(ws.operationName());
        
        return annot;
    }

    public boolean hasWebResultAnnotation(Method method)
    {
        return method.isAnnotationPresent(WebResult.class);
    }

    public WebResultAnnotation getWebResultAnnotation(Method method)
    {
        WebResult ws = (WebResult) method.getAnnotation(WebResult.class);
        
        WebResultAnnotation annot = new WebResultAnnotation();
        annot.setName(ws.name());
        annot.setTargetNameSpace(ws.targetNamespace());
        
        return annot;
    }

    public boolean hasWebParamAnnotation(Method method, int arg1)
    {
        return method.isAnnotationPresent(WebParam.class);
    }

    public WebParamAnnotation getWebParamAnnotation(Method method, int arg1)
    {
        WebParam ws = (WebParam) method.getAnnotation(WebParam.class);
        
        WebParamAnnotation annot = new WebParamAnnotation();
        annot.setName(ws.name());
        annot.setTargetNamespace(ws.targetNamespace());
        annot.setHeader(ws.header());
        
        if (ws.mode() == WebParam.Mode.IN)
            annot.setMode(WebParamAnnotation.MODE_IN);
        else if (ws.mode() == WebParam.Mode.INOUT)
            annot.setMode(WebParamAnnotation.MODE_INOUT);
        else if (ws.mode() == WebParam.Mode.OUT)
            annot.setMode(WebParamAnnotation.MODE_OUT);
        
        return annot;
    }

}
