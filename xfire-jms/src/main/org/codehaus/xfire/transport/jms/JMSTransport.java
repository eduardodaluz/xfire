package org.codehaus.xfire.transport.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.AbstractWSDLTransport;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.SoapServiceEndpoint;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl11.WSDL11Transport;

public class JMSTransport
    extends AbstractWSDLTransport
    implements Transport, WSDL11Transport
{
    private static final Log log = LogFactory.getLog(JMSTransport.class);
    
    public final static String NAME = "JMS";
    
    private static final String URI_PREFIX = "";
    
    private Destination source;
    private Destination destination;
    private Destination errors;
    private ConnectionFactory connectionFactory;

  /**
   * @param factory The JMS ConnectionFactory
   */
    public JMSTransport(ConnectionFactory factory)
    {
        this.connectionFactory = factory;
    }

    public String getName()
    {
        return NAME;
    }

    public String getServiceURL(Service service)
    {
        return "jms/" + service.getName();
    }

    public String getTransportURI(Service service)
    {
        return "jms://soap";
    }

    protected Channel createNewChannel(String uri, Service service)
    {
        log.debug("Creating new channel for uri: " + uri);
        
        Channel c = new JMSChannel(uri, this);
        
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

    public ConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }
  
    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }
}