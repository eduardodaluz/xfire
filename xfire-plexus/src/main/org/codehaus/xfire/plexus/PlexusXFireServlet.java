package org.codehaus.xfire.plexus;

import javax.servlet.ServletException;

import org.codehaus.plexus.servlet.PlexusServletUtils;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.XFireServlet;

/**
 * Loads XFire and processes requests via a servlet.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class PlexusXFireServlet
    extends XFireServlet
{
    private XFire xfire;

    protected TransportManager getTransportManager()
    	throws ServletException
    {
        return getXFire().getTransportManager();
    }

	public XFire getXFire()
        throws ServletException
	{
        if ( xfire == null )
        {
            xfire = (XFire) PlexusServletUtils.lookup(getServletContext(), XFire.ROLE);
        }
        return xfire;
	}
    
    public ServiceRegistry getServiceRegistry() 
        throws ServletException
    {
        return getXFire().getServiceRegistry();
    }
}
