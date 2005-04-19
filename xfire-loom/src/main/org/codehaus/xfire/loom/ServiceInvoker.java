package org.codehaus.xfire.loom;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.binding.ObjectInvoker;

/**
 * Invokes a Loom service.
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class ServiceInvoker extends ObjectInvoker
{
    private final Object m_service;

    public ServiceInvoker( final Object service )
    {
        m_service = service;
    }

    public Object getServiceObject( final MessageContext context ) throws XFireFault
    {
        return m_service;
    }
}
