package org.codehaus.xfire.spring.config;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author tomeks
 * @org.apache.xbean.XBean element="handlerName"
 */
public class HandlerNameBean
    implements FactoryBean
{

    private String handlerName;
    
    public Object getObject()
        throws Exception
    {
        return handlerName;
    }

    public Class getObjectType()
    {

        return String.class;
    }

    public boolean isSingleton()
    {
        return false;
    }

    public String getHandler()
    {
        return handlerName;
    }

    public void setHandler(String handlerName)
    {
        this.handlerName = handlerName;
    }

 

}
