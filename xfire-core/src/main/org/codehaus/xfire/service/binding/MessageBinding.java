package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.EndpointHandler;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceEndpoint;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Handles java services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public class MessageBinding
    extends AbstractBinding
    implements EndpointHandler, ObjectBinding
{
    private static final Log logger = LogFactory.getLog(MessageBinding.class.getName());

    public MessageBinding()
    {
        setStyle(SoapConstants.STYLE_MESSAGE);
        setUse(SoapConstants.USE_LITERAL);
    }

    public Object[] read(MessageContext context)
        throws XFireFault
    {
        final ServiceEndpoint endpoint = context.getService();
        final OperationInfo operation = (OperationInfo) endpoint.getService().getOperations().iterator().next();
        final Invoker invoker = getInvoker();

        context.setProperty(OPERATION_KEY, operation);
        
        final List params = new ArrayList();
        
        for (Iterator itr = operation.getInputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();
            
            params.add( getBindingProvider().readParameter(p, context) );
        }
        
        return params.toArray();
    }

    public void write(Object[] values, MessageContext context)
        throws XFireFault
    {
        final OperationInfo operation = getOperation(context);
        
        int i = 0;
        for (Iterator itr = operation.getInputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();
            
            getBindingProvider().writeParameter(p, context, values[i]);
            i++;
        }
    }
}
