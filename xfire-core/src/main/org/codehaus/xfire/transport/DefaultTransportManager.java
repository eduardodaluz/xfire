package org.codehaus.xfire.transport;

import java.util.HashSet;
import java.util.Set;
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
    Set transports;
    
    public DefaultTransportManager()
    {
        transports = new HashSet();
    }
    
    /**
     * @see org.codehaus.xfire.transport.TransportManager#register(org.codehaus.xfire.transport.Transport)
     */
    public void register(Transport transport)
    {
        transports.add(transport);
    }

    /**
     * @see org.codehaus.xfire.transport.TransportManager#getTransports(java.lang.String)
     */
    public Set getTransports(String service)
    {
        return transports;
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

}
