package org.codehaus.xfire.spring;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.object.ObjectInvoker;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 9, 2005
 */
public class BeanInvoker
    extends ObjectInvoker
{
    private Object proxy;
    
    public BeanInvoker(Object proxy)
    {
        this.proxy = proxy;
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
        return proxy;
    }
}
