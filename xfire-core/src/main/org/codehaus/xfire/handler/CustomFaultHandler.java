package org.codehaus.xfire.handler;

import java.util.Iterator;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.binding.ObjectBinding;
import org.codehaus.xfire.util.ElementStreamWriter;

/**
 * Builds up a custom detail element from an exception.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class CustomFaultHandler
    extends AbstractHandler
{    
    public String getPhase()
    {
        return Phase.USER;
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
            ObjectBinding binding = context.getService().getBinding();

            ElementStreamWriter writer = new ElementStreamWriter(fault.getDetail());
            
            binding.getBindingProvider().writeParameter(faultPart, writer, context, cause);
        }
    }

    public MessagePartInfo getFaultForClass(OperationInfo op, Class class1)
    {
        for (Iterator itr = op.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            MessagePartInfo info = (MessagePartInfo) faultInfo.getMessageParts().get(0);
            
            if (info.getTypeClass().equals(class1))
                return info;
        }
        
        return null;
    }
}
