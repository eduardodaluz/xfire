package org.codehaus.xfire.xmpp;

import org.codehaus.xfire.fault.FaultHandlerPipeline;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl11.WSDL11Transport;
import org.jivesoftware.smack.XMPPConnection;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMPPTransport
    extends AbstractTransport
    implements Transport, WSDL11Transport
{
    public final static String NAME = "XMPP";
    public final static String XMPP_TRANSPORT_NS = "http://jabber.org/protocol/soap";
    
    private String id;
    
    public XMPPTransport(XMPPConnection conn)
    {
        this.id = conn.getUser();
        
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
        return id;
    }

    /**
     * @param service
     * @return
     */
    public String getTransportURI(Service service)
    {
        return XMPP_TRANSPORT_NS;
    }
}
