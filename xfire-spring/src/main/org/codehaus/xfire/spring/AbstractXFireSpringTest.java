package org.codehaus.xfire.spring;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.ServiceFactory;
import org.springframework.context.ApplicationContext;

/**
 * Provides test support for XFire spring web services. Simply supply your ApplicationContext by
 * implementing the <code>createContext</code> method.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractXFireSpringTest
    extends AbstractXFireAegisTest
{
    ApplicationContext context;
    
    public void setUp() 
        throws Exception
    {
        context = createContext();
        
        super.setUp();
    }

    protected abstract ApplicationContext createContext();

    protected ApplicationContext getContext()
    {
        return context;
    }
    
    public ServiceFactory getServiceFactory()
    {
        return (ServiceFactory) context.getBean("xfire.serviceFactory");
    }

    protected XFire getXFire()
    {
        return (XFire) context.getBean("xfire");
    }
    
    protected Object getBean(String id)
    {
        return context.getBean(id);
    }
}
