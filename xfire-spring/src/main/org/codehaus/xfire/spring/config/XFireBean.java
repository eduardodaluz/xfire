package org.codehaus.xfire.spring.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.Transport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 * @org.xbean.XBean element="xfire"
 */
public class XFireBean implements InitializingBean, ApplicationContextAware
{
    private List inHandlers = new ArrayList();

    private List outHandlers = new ArrayList();

    private List faultHandlers = new ArrayList();

    private List transports = new ArrayList();

    protected XFire xFire;
    
    public List getFaultHandlers()
    {
        return faultHandlers;
    }

    public void setFaultHandlers(List faultHandlers)
    {
        this.faultHandlers = faultHandlers;
    }

    public List getInHandlers()
    {
        return inHandlers;
    }

    public void setInHandlers(List inHandlers)
    {
        this.inHandlers = inHandlers;
    }

    public List getOutHandlers()
    {
        return outHandlers;
    }

    public void setOutHandlers(List outHandlers)
    {
        this.outHandlers = outHandlers;
    }
    
    public List getTransports()
    {
        return transports;
    }

    public void setTransports(List transports)
    {
        this.transports = transports;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        xFire.getInHandlers().addAll(getInHandlers());
        xFire.getOutHandlers().addAll(getOutHandlers());
        xFire.getFaultHandlers().addAll(getFaultHandlers());
        
        for (Iterator itr = transports.iterator(); itr.hasNext();)
        {
            Transport t = (Transport) itr.next();
            
            xFire.getTransportManager().register(t);
        }
    }

    public void setApplicationContext(ApplicationContext ctx)
        throws BeansException
    {
        xFire = (XFire) ctx.getBean("xfire");
    } 
}
