package org.codehaus.xfire.handler;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.util.stax.ElementStreamWriter;

/**
 * Builds up a custom detail element from an exception.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class CustomFaultHandler
    extends AbstractHandler
{    
    
    public CustomFaultHandler() {
        super();
        setPhase(Phase.USER);
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        XFireFault fault = (XFireFault) context.getExchange().getFaultMessage().getBody();
        
        Throwable cause = fault.getCause();
        MessagePartInfo faultPart = null;
        OperationInfo op = null;
        
        if (context.getExchange() != null) 
            op = context.getExchange().getOperation();

        if (cause == null || op == null)
            return;

        faultPart = getFaultForClass(op, cause.getClass());

        if (faultPart != null)
        {
            handleFault(context, fault, cause, faultPart);
        }
    }

    protected void handleFault(MessageContext context, XFireFault fault, Throwable cause, MessagePartInfo faultPart)
        throws XFireFault
    {
        ElementStreamWriter writer = new ElementStreamWriter(fault.getDetail());
        
        try
        {
            AbstractBinding.writeParameter(writer, context, cause, faultPart, faultPart.getName().getNamespaceURI());
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not write to outgoing stream.", e, XFireFault.RECEIVER);
        }
    }

    /**
     * Find the correct Fault part for a particular exception.
     * @param op
     * @param class1
     * @return
     */
    public MessagePartInfo getFaultForClass(OperationInfo op, Class class1)
    {
        for (Iterator itr = op.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            MessagePartInfo info = (MessagePartInfo) faultInfo.getMessageParts().get(0);
            
            if (class1.isAssignableFrom(info.getTypeClass()))
                return info;
        }
        
        return null;
    }
}
