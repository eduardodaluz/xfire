package org.codehaus.xfire;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.soap.SoapVersionFactory;
import org.codehaus.xfire.transport.Session;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.yom.Element;

/**
 * Holds inforrmation about the message request and response.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class MessageContext
{
    private static ThreadLocal messageContexts = new ThreadLocal();

    private Session session;
    private OutputStream responseStream;
    private String requestUri;
    private String serviceName;
    private String action;
    private Map properties;

    private SoapVersion soapVersion;
    private Service service;
    private Transport transport;
    private XMLStreamReader xmlStreamReader;

    private Element requestHeader;
    private Element responseHeader;

    private MessageDestination replyDestination;
    private MessageDestination faultDestination;

    public MessageContext()
    {
        properties = new HashMap();
        messageContexts.set(this);
    }

    /**
     * Create a MessageContext to invoke a service with the specified document as the request.
     */
    public MessageContext(String service,
                          String action,
                          OutputStream response,
                          Session session,
                          String requestUri)
    {
        messageContexts.set(this);

        properties = new HashMap();

        this.serviceName = service;
        this.action = action;
        this.session = session;
        this.requestUri = requestUri;

        this.replyDestination = new MessageDestination(response, requestUri);
        this.faultDestination = replyDestination;
    }

    public MessageDestination getFaultDestination()
    {
        return faultDestination;
    }

    public void setFaultDestination(MessageDestination faultDestination)
    {
        this.faultDestination = faultDestination;
    }

    public MessageDestination getReplyDestination()
    {
        return replyDestination;
    }

    public void setReplyDestination(MessageDestination replyDestination)
    {
        this.replyDestination = replyDestination;
    }

    /**
     * Get the message context for the current thread.
     *
     * @return The current MessageContext or null if there is none.
     */
    public static MessageContext getCurrentMessageContext()
    {
        return (MessageContext) messageContexts.get();
    }

    public Object getProperty(Object key)
    {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value)
    {
        properties.put(key, value);
    }

    public String getRequestUri()
    {
        return requestUri;
    }

    public void setRequestUri(String requestUri)
    {
        this.requestUri = requestUri;
    }

    public void setRequestStream(InputStream requestStream)
    {
        try
        {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(requestStream);
            setXMLStreamReader(xmlStreamReader);
        }
        catch (XMLStreamException e)
        {
            throw new IllegalArgumentException("Invalid xml request stream: " + e);
        }
    }

    /**
     * The session that this request is a part of.
     *
     * @return
     */
    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String service)
    {
        this.serviceName = service;
    }

    public SoapVersion getSoapVersion()
    {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion)
    {
        this.soapVersion = SoapVersionFactory.getInstance().getSoapVersion(soapVersion);
    }

    /**
     * The service being invoked.
     *
     * @return
     */
    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    /**
     * @return Returns the xmlStreamReader.
     */
    public XMLStreamReader getXMLStreamReader()
    {
        return xmlStreamReader;
    }

    /**
     * @param xmlStreamReader The xmlStreamReader to set.
     */
    public void setXMLStreamReader(XMLStreamReader xmlStreamReader)
    {
        this.xmlStreamReader = xmlStreamReader;
    }

    /**
     * @return Returns the transport.
     */
    public Transport getTransport()
    {
        return transport;
    }

    /**
     * @param transport The transport to set.
     */
    public void setTransport(Transport transport)
    {
        this.transport = transport;
    }

    public Element getRequestHeader()
    {
        return requestHeader;
    }

    public void setRequestHeader(Element requestHeader)
    {
        this.requestHeader = requestHeader;
    }

    public Element getResponseHeader()
    {
        return responseHeader;
    }

    public void setResponseHeader(Element responseHeader)
    {
        this.responseHeader = responseHeader;
    }
}
