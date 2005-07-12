package org.codehaus.xfire.examples.router;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Service;
import org.codehaus.yom.Element;

public class ServiceRouterHandler
    extends AbstractHandler
{
    public final static String VERSION_NS = "http://xfire.codehaus.org/examples/router";
    public final static String VERSION_NAME = "Version";
    
    private final static String DEFAULT_VERSION = "1";
    
    public String getPhase()
    {
        return Phase.PRE_DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Element header = context.getInMessage().getHeader();
        if (header == null) setVersion(DEFAULT_VERSION, context);
        
        Element versionEl = header.getFirstChildElement(VERSION_NAME, VERSION_NS);
        if (versionEl == null) setVersion(DEFAULT_VERSION, context);
        
        String version = versionEl.getValue();
        if (version == null || version.length() == 0)
        {
            throw new XFireFault("An empty version element is not allowed.", XFireFault.SENDER);
        }
        
        setVersion(version, context);
    }

    private void setVersion(String version, MessageContext context) 
        throws XFireFault
    {
        Service service = context.getXFire().getServiceRegistry().getService("Echo" + version);
        
        if (service == null)
        {
            throw new XFireFault("Invalid version: " + version, XFireFault.SENDER);
        }
        
        context.setService(service);
    }

}
