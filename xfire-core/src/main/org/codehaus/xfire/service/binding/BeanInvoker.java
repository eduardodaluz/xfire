package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

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
     * @param context
     * @return
     * @throws XFireFault
     * @see org.codehaus.xfire.service.binding.ObjectInvoker#getServiceObject(org.codehaus.xfire.MessageContext)
     */
    public Object getServiceObject(MessageContext context)
            throws XFireFault
    {
        return proxy;
    }
}
