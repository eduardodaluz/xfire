package org.codehaus.xfire.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractWSDLTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPTransport
    extends AbstractWSDLTransport
    implements Transport, WSDL11Transport
{
    private static final Log log = LogFactory.getLog(XMPPTransport.class);
    
    public final static String NAME = "XMPP";
    public final static String XMPP_TRANSPORT_NS = "http://jabber.org/protocol/soap";

    private XFire xfire;
    private String username;
    private String password;
    private String server;
    private String id;
    
    public XMPPTransport(XFire xfire, String server, String username, String password)
    {
        this.username = username;
        this.password = password;
        this.server = server;
        this.xfire = xfire;
        
        this.id = username + "@" + server;
        
        // Make sure the SoapIQProvider class has been loaded so
        // our IQ provider is registered.
        new SoapIQProvider();
        
        addFaultHandler(new XMPPFaultHandler());
    }

    /**
     * Gets the transport name. @see NAME.
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * @param service
     * @return
     */
    public String getServiceURL(Service service)
    {
        try
        {
            return id + "/" + service.getName();
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't create the channel.", e);
        }
    }

    /**
     * @param service
     * @return
     */
    public String getTransportURI(Service service)
    {
        return XMPP_TRANSPORT_NS;
    }

    protected Channel createNewChannel(String uri)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        XMPPChannel c = new XMPPChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    protected String getUriPrefix()
    {
        return id;
    }

    public String getPassword()
    {
        return password;
    }
    
    public XFire getXFire()
    {
        return xfire;
    }

    public String getServer()
    {
        return server;
    }

    public String getUsername()
    {
        return username;
    }

    public String[] getKnownUriSchemes()
    {
        return new String[] { "xmpp://" };
    }

}
