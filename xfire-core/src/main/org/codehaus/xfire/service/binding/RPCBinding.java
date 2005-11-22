package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;

public class RPCBinding
    extends WrappedBinding
{
    public RPCBinding()
    {
    }

    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());
        
        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        String opName = dr.getLocalName();
        OperationInfo operation = endpoint.getServiceInfo().getOperation( opName );
        if (operation == null)
        {
            // Determine the operation name which is in the form of:
            // xxxxRequest where xxxx is the operation.
            int index = opName.indexOf("Request");
            if (index > 0)
            {
                operation = endpoint.getServiceInfo().getOperation( opName.substring(0, index) );
            }
        }
        
        if (operation == null)
            throw new XFireFault("Could not find appropriate operation!", XFireFault.SENDER);
        
        // Move from operation element to whitespace or start element
        nextEvent(dr);
        
        setOperation(operation, context);

        if (operation == null)
        {
            throw new XFireFault("Invalid operation.", XFireFault.SENDER);
        }

        while(STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = operation.getInputMessage().getMessagePart(dr.getName());

            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( endpoint.getBindingProvider().readParameter(p, dr, context) );
        }
        
        context.getInMessage().setBody(parameters);
    }

    public Object clone()
    {
        return new RPCBinding();
    }    
}
