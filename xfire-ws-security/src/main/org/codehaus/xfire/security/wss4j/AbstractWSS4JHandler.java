package org.codehaus.xfire.security.wss4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandler;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.Handler;

public abstract class AbstractWSS4JHandler extends WSHandler implements Handler
{
    private Map properties = new HashMap();
    private List before = new ArrayList();
    private List after = new ArrayList();
    private String phase;
    
    public String getPhase()
    {
        return phase;
    }

    public void setPhase(String phase)
    {
        this.phase = phase;
    }

    public Object getOption(String key)
    {
        return properties.get(key);
    }

    public void setProperty(String key, String value)
    {
        properties.put(key, value);
    }
    
    public String getPassword(Object msgContext)
    {
        return (String) ((MessageContext) msgContext).getContextualProperty("password");
    }

    public Object getProperty(Object msgContext, String key)
    {
        return ((MessageContext) msgContext).getContextualProperty(key);
    }

    public void setPassword(Object msgContext, String password)
    {
        ((MessageContext) msgContext).setProperty("password", password);
    }

    public void setProperty(Object msgContext, String key, Object value)
    {
        ((MessageContext) msgContext).setProperty(key, value);
    }
    
    public QName[] getUnderstoodHeaders()
    {
        return new QName[] {
            new QName(WSConstants.WSSE_NS, "Security"),
            new QName(WSConstants.WSSE11_NS, "Security")
        };
    }

    public Map getProperties()
    {
        return properties;
    }

    public void setProperties(Map properties)
    {
        this.properties = properties;
    }

    public String[] getRoles()
    {
        return null;
    }

    public void handleFault(XFireFault fault, MessageContext context)
    {
    }

    public List getAfter()
    {
        return after;
    }

    public void setAfter(List after)
    {
        this.after = after;
    }

    public List getBefore()
    {
        return before;
    }

    public void setBefore(List before)
    {
        this.before = before;
    }
}
