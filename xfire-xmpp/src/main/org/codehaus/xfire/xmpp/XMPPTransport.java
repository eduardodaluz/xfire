package org.codehaus.xfire.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.AbstractWSDLTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.SoapServiceEndpoint;
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
    
    private static final String URI_PREFIX = "";
    
    private ServiceRegistry registry;
    private String username;
    private String password;
    private String server;
    private String id;
    
    public XMPPTransport(ServiceRegistry registry, String server, String username, String password)
    {
        this.username = username;
        this.password = password;
        this.server = server;
        this.registry = registry;
        
        this.id = username + "@" + server;
        
        // Make sure the SoapIQProvider class has been loaded so
        // our IQ provider is registered.
        SoapIQProvider.class.getName();
        
        FaultHandlerPipeline pipeline = new FaultHandlerPipeline();
        pipeline.addHandler(new XMPPFaultHandler());
        
        setFaultPipeline(pipeline);
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
            return id + "/" + createChannel(service).getUri();
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

    protected Channel createNewChannel(String uri, Service service)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        XMPPChannel c = new XMPPChannel(uri, this);
        
        if (service != null)
        {
            c.setService(service);
            c.setEndpoint(new SoapServiceEndpoint());
        }

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    public String getPassword()
    {
        return password;
    }
    
    public ServiceRegistry getRegistry()
    {
        return registry;
    }

    public String getServer()
    {
        return server;
    }

    public String getUsername()
    {
        return username;
    }

}
