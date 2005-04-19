package org.codehaus.xfire.service.bridge;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.service.binding.Operation;
import org.codehaus.xfire.soap.SoapHandler;

/**
 * Basic message bridging functionality.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractMessageBridge
    implements MessageBridge
{
    private ObjectService service;

    private MessageContext context;
    
    private XMLStreamReader request;

    protected Operation operation;
    
    public AbstractMessageBridge()
    {
    }
    
    public AbstractMessageBridge(MessageContext context)
    {
        this.service = (ObjectService) context.getService();
        this.context = context;
        this.request = context.getXMLStreamReader();
    }

    /**
     * @return Returns the service.
     */
    public ObjectService getService()
    {
        return service;
    }

    /**
     * @return Returns the context.
     */
    public MessageContext getContext()
    {
        return context;
    }

    /**
     * @param context
     *            The context to set.
     */
    public void setContext(MessageContext context)
    {
        this.context = context;
    }

    public XMLStreamReader getRequestReader()
    {
        return request;
    }

    /**
     * @return Returns the response body.
     */
    public XMLStreamWriter getResponseWriter()
    {
        return (XMLStreamWriter) context.getProperty(SoapHandler.STAX_WRITER_KEY);
    }

    /**
     * @see org.codehaus.xfire.aegis.bridge.MessageBridge#getOperation()
     */
    public Operation getOperation()
    {
        return operation;
    }

    /**
     * @param operation
     *            The operation to set.
     */
    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }
}
