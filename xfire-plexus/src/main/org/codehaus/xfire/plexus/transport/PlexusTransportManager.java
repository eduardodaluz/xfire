package org.codehaus.xfire.plexus.transport;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusTransportManager
    extends DefaultTransportManager
    implements Initializable
{
    private ServiceRegistry registry;

    public PlexusTransportManager()
    {
        super();
    }

    /**
     * @throws Exception
     */
    public void initialize()
        throws Exception
    {
        initializeTransports(registry);
    }
}
