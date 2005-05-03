package org.codehaus.xfire.service.bridge;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.MessageContext;
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
public class RPCEncodedBridge
    extends WrappedBridge
{
    public RPCEncodedBridge(MessageContext context)
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
        
        String opName = dr.getLocalName();
        OperationInfo operation = getService().getService().getOperation( opName );
        if (operation == null)
        {
            // Determine the operation name which is in the form of:
            // xxxxRequest where xxxx is the operation.
            int index = opName.indexOf("Request");
            if (index > 0)
            {
                operation = getService().getService().getOperation( opName.substring(0, index) );
            }
        }
        
        setOperation(operation);

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

            parameters.add( getService().getBindingProvider().readParameter(p, getContext()) );
        }
        
        return parameters;
    }
}
