package org.codehaus.xfire.loom;

import javax.servlet.ServletException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServlet;

/**
 * An XFire servlet which obtains its XFire instance from a ServiceManager
 *
 * @author <a href="mailto:peter.royal@pobox.com.com">peter.royal</a>
 */
public class LoomXFireServlet extends XFireServlet implements Serviceable
{
    private XFire m_xfire;

    public void service( final ServiceManager manager ) throws ServiceException
    {
        m_xfire = (XFire)manager.lookup( XFire.ROLE );
    }

    public XFire createXFire()
        throws ServletException
    {
        return m_xfire;
    }
}
