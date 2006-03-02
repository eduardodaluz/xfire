package org.codehaus.xfire.loom;


import org.codehaus.xfire.service.invoker.BeanInvoker;

/**
 * Invokes a Loom service.
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 */
public class ServiceInvoker extends BeanInvoker
{
    //private final Object m_service;

    public ServiceInvoker( final Object service )
    {
      super(service);
      //  m_service = service;
    }
/*
    public Object getServiceObject( final MessageContext context ) throws XFireFault
    {
        return m_service;
    }*/
}
