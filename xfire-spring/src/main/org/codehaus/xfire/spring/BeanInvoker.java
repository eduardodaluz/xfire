package org.codehaus.xfire.spring;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.object.ObjectInvoker;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 9, 2005
 */
public class BeanInvoker
    extends ObjectInvoker
{
    private BeanFactory factory;
    private String name;
    
    public BeanInvoker(BeanFactory factory, String name)
    {
       this.factory = factory;
       this.name = name;
    }
    
    /**
     * @see org.codehaus.xfire.service.object.ObjectInvoker#getServiceObject(org.codehaus.xfire.MessageContext)
     * @param context
     * @return
     * @throws XFireFault
     */
    public Object getServiceObject(MessageContext context)
        throws XFireFault
    {
        return factory.getBean(name);
    }
}
