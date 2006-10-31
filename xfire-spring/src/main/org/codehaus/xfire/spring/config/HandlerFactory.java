package org.codehaus.xfire.spring.config;

import java.util.Collection;
import java.util.Iterator;

import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Handler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.apache.xbean.XBean element="handler"
 */
public class HandlerFactory
    implements FactoryBean, InitializingBean
{

    private static final Log log = LogFactory.getLog(HandlerFactory.class);

    private Class handlerClass;

    private Handler handler;

    private Collection before;

    private Collection after;

    public Collection getBefore()
    {
        return before;
    }

    /**
     * @param before
     */
    public void setBefore(Collection before)
    {
        this.before = before;
    }

    public Collection getAfter()
    {
        return after;
    }

    public void setAfter(Collection after)
    {
        this.after = after;
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
            log.error("Can't create instance of :" + handlerClass);
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
            if(!(handler instanceof AbstractHandler )){
                String msg ="Handler "+ handler.getClass().getName()+" is not instance of AbstractHandler!";
                log.error(msg);
                throw new RuntimeException(msg);
            }
            AbstractHandler aHandler = (AbstractHandler) handler;
            for( Iterator iter = before.iterator(); iter.hasNext();){
                aHandler.before((String)iter.next());
            }
        }
        if (after!= null)
        {
            if(!(handler instanceof AbstractHandler )){
                String msg = "Handler "+ handler.getClass().getName()+" is not instance of AbstractHandler!";
                log.error(msg);
                throw new RuntimeException(msg);
            }
            AbstractHandler aHandler = (AbstractHandler) handler;
            for( Iterator iter = after.iterator(); iter.hasNext();){
                aHandler.after((String)iter.next());
            }
        }

    }
}
