package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.soap.SoapVersionFactory;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Handles messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Feb 18, 2004
 */
public class MessageBinding
    extends AbstractBinding
{
    private static final Log logger = LogFactory.getLog(MessageBinding.class.getName());

    public MessageBinding()
    {
        setStyle(SoapConstants.STYLE_MESSAGE);
        setUse(SoapConstants.USE_LITERAL);
    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        final Service endpoint = context.getService();
        
        OperationInfo operation = null;
        
        if (isClientModeOn())
        {
            operation = context.getExchange().getOperation();
        }
        else
        {
            operation = (OperationInfo) endpoint.getServiceInfo().getOperations().iterator().next();
            
            setOperation(operation, context);
        }

        DepthXMLStreamReader dr = new DepthXMLStreamReader(message.getXMLStreamReader());
        STAXUtils.toNextElement(dr);
        checkAndHandleFault(dr);
        
        final Invoker invoker = getInvoker();

        final List params = new ArrayList();
        
        for (Iterator itr = operation.getInputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();

            params.add( getBindingProvider().readParameter(p, message.getXMLStreamReader(), context) );
        }

        message.setBody( params.toArray() );
    }

    private void checkAndHandleFault(DepthXMLStreamReader dr)
    {
        SoapVersionFactory factory = SoapVersionFactory.getInstance();
        for (Iterator itr = factory.getVersions(); itr.hasNext();)
        {
            SoapVersion version = (SoapVersion) itr.next();
            
            if (dr.getName().equals(version.getFault()))
            {
            
            }
        }
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        Object[] values = (Object[]) message.getBody();
        final OperationInfo operation = context.getExchange().getOperation();
        
        int i = 0;
        for (Iterator itr = operation.getOutputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo p = (MessagePartInfo) itr.next();
            
            getBindingProvider().writeParameter(p, writer, context, values[i]);
            i++;
        }
    }

    public Object clone()
    {
        return new MessageBinding();
    }    
}
