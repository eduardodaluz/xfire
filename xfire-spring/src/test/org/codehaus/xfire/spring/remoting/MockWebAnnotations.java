package org.codehaus.xfire.spring.remoting;

import java.lang.reflect.Method;

import org.codehaus.xfire.annotations.HandlerChainAnnotation;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebMethodAnnotation;
import org.codehaus.xfire.annotations.WebParamAnnotation;
import org.codehaus.xfire.annotations.WebResultAnnotation;
import org.codehaus.xfire.annotations.WebServiceAnnotation;
import org.codehaus.xfire.annotations.soap.SOAPBindingAnnotation;
import org.codehaus.xfire.test.EchoImpl;

public class MockWebAnnotations implements WebAnnotations
{

    public HandlerChainAnnotation getHandlerChainAnnotation(Class aClass)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SOAPBindingAnnotation getSOAPBindingAnnotation(Class aClass)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public WebMethodAnnotation getWebMethodAnnotation(Method method)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public WebParamAnnotation getWebParamAnnotation(Method method, int parameter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public WebResultAnnotation getWebResultAnnotation(Method method)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public WebServiceAnnotation getWebServiceAnnotation(Class aClass)
    {
        return new WebServiceAnnotation();
    }

    public boolean hasHandlerChainAnnotation(Class aClass)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasOnewayAnnotation(Method method)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasSOAPBindingAnnotation(Class aClass)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasWebMethodAnnotation(Method method)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasWebParamAnnotation(Method method, int parameter)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasWebResultAnnotation(Method method)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasWebServiceAnnotation(Class aClass)
    {
        return aClass.equals(EchoImpl.class);
    }

}
