package org.codehaus.xfire.jaxws;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.util.stax.DepthXMLStreamReader;

public class JAXWSBinding 
    extends AbstractBinding
    implements MessageSerializer
{
    private MessageSerializer delegate;
    private Map<OperationInfo, JAXWSOperationBinding> op2Binding = 
        new HashMap<OperationInfo, JAXWSOperationBinding>();
    
    public JAXWSBinding(MessageSerializer delegate)
    {
        super();

        this.delegate = delegate;
    }

    public void readMessage(InMessage message, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());

        if ( !STAXUtils.toNextElement(dr) )
            throw new XFireFault("There must be a method name element.", XFireFault.SENDER);
        
        OperationInfo op = context.getExchange().getOperation();

        if (!isClientModeOn(context) && op == null)
        {
            op = endpoint.getServiceInfo().getOperation( dr.getLocalName() );

            setOperation(op, context);

            JAXWSOperationBinding opBinding = getOperationBinding(op);
            opBinding.readMessage(message, context);
        }
        else
        {
            delegate.readMessage(message, context);
        }
    }

    private JAXWSOperationBinding getOperationBinding(OperationInfo op)
    {
        JAXWSOperationBinding opBinding = (JAXWSOperationBinding) op2Binding.get(op);
        if (opBinding == null)
        {
            opBinding = new JAXWSOperationBinding(op, delegate);
            op2Binding.put(op, opBinding);
        }
        return opBinding;
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        OperationInfo op = context.getExchange().getOperation();
        
    }
}
