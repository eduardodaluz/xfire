package org.codehaus.xfire.spring;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServletController;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SpringXFireController
    extends XFireServletController
{
    private String serviceName;

    /**
     * @param xfire
     */
    public SpringXFireController(XFire xfire, String serviceName)
    {
        super(xfire);
        this.serviceName = serviceName;
    }

    protected String getService(HttpServletRequest request)
    {
        return serviceName;
    }
}
