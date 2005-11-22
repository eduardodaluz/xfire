package org.codehaus.xfire.transport.jms;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.soap.SoapTransportHelper;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.DefaultEndpoint;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

public class JMSTransport
    extends AbstractTransport
    implements SoapTransport, WSDL11Transport
{
    private static final Log log = LogFactory.getLog(JMSTransport.class);
    
    public final static String NAME = "JMS";
    
    private static final String BINDING_ID = "urn:xfire:jms";
    private static final String URI_PREFIX = "";

    private ConnectionFactory connectionFactory;
    private XFire xfire;
    
  /**
   * @param factory The JMS ConnectionFactory
   */
    public JMSTransport(XFire xfire, ConnectionFactory factory)
    {
        this.xfire = xfire;
        this.connectionFactory = factory;
        
        SoapTransportHelper.createSoapTransport(this);
    }

    public String getName()
    {
        return NAME;
    }

    public String getServiceURL(Service service)
    {
        return "jms://" + service.getName();
    }

    public String getTransportURI(Service service)
    {
        return "jms://soap";
    }

    protected Channel createNewChannel(String uri)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        Channel c = new JMSChannel(uri, this);
        c.setEndpoint(new DefaultEndpoint());

        return c;
    }

    protected String getUriPrefix()
    {
        return URI_PREFIX;
    }

    public ConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }
  
    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    public XFire getXFire()
    {
        return xfire;
    }

    public String[] getKnownUriSchemes()
    {
        return new String[] { "jms://" };
    }

    public String[] getSupportedBindings()
    {
        return new String[] { BINDING_ID };
    }
    
}