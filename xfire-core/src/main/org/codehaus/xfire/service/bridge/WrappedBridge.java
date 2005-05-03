package org.codehaus.xfire.service.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Reads Document/Literal style messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public class WrappedBridge
    extends AbstractMessageBridge
{
    public WrappedBridge(MessageContext context)
    {
        super(context);
	}
    
    public List read() 
    	throws XFireFault
    {
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(getRequestReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        OperationInfo op = getService().getService().getOperation( dr.getLocalName() );
        setOperation(op);

        if (op == null)
        {
            throw new XFireFault("Invalid operation.", XFireFault.SENDER);
        }

        while(STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = op.getInputMessage().getMessagePart(dr.getName());

            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getService().getBindingProvider().readParameter(p, getContext()) );
        }
        
        return parameters;
    }

    /**
     * @see org.codehaus.xfire.service.bridge.MessageBridge#write(java.lang.Object)
     */
    public void write(Object[] values)
    	throws XFireFault
    {
        try
        {
            String name = getOperation().getName() + "Response";
            writeStartElement(getResponseWriter(), name, getService().getService().getName().getNamespaceURI());
            
            int i = 0;
            for(Iterator itr = getOperation().getOutputMessage().getMessageParts().iterator(); itr.hasNext();)
            {
                MessagePartInfo outParam = (MessagePartInfo) itr.next();
    
                getService().getBindingProvider().writeParameter(outParam, getContext(), values[i]);
                i++;
            }
    
            getResponseWriter().writeEndElement();
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't write start element.", e);
        }
    }
    
    public void writeStartElement(XMLStreamWriter writer, String name, String namespace) 
        throws XMLStreamException
    {
        String prefix = "";
        
        writer.setPrefix(prefix, namespace);
        writer.writeStartElement(prefix, name, namespace);
        writer.writeNamespace(prefix, namespace);
    }
}
