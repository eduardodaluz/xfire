package org.codehaus.xfire.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.AbstractXFireComponent;

/**
 * The default <code>TransportService</code> implementation.
 * 
 * TODO implement enable/disable.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultTransportManager
	extends AbstractXFireComponent
    implements TransportManager
{
    Map transports;
    
    public DefaultTransportManager()
    {
        transports = new HashMap();
    }
    
    /**
     * @see org.codehaus.xfire.transport.TransportManager#register(org.codehaus.xfire.transport.Transport)
     */
    public void register(Transport transport)
    {
        transports.put(transport.getName(), transport);
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     */
    public Transport getTransport(String name)
    {
        return (Transport) transports.get(name);
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#enable(java.lang.String, java.lang.String)
     */
    public void enable(String transport, String service)
    {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#disable(java.lang.String, java.lang.String)
     */
    public void disable(String transport, String service)
    {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     * @param service
     * @return
     */
    public Collection getTransports(String service)
    {
        return transports.values();
    }
}
