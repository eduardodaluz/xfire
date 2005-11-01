package org.codehaus.xfire.spring.config;

import java.lang.reflect.Method;

import org.codehaus.xfire.handler.Handler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="handler"
 */
public class HandlerFactory
    implements FactoryBean, InitializingBean
{

    private Class handlerClass;

    private Handler handler;

    private String before;

    public String getBefore()
    {
        return before;
    }

    /**
     * @param before
     */
    public void setBefore(String before)
    {
        this.before = before;
    }

    /**
     * @return
     */
    public Class getHandlerClass()
    {
        return handlerClass;
    }

    /**
     * @param handlerClass
     */
    public void setHandlerClass(Class handlerClass)
    {
        try
        {
            handler = (Handler) handlerClass.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't create instance of :" + handlerClass);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
        throws Exception
    {

        return handler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType()
    {
        return handler.getClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return false;
    }

    /**/
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        if (before != null)
        {
            Method method = handler.getClass().getMethod("setBefore", new Class[] { String.class });
            method.invoke(handler, new Object[] { before });
        }
    }
}
