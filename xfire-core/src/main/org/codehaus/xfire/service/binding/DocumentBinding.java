package org.codehaus.xfire.service.binding;

import java.util.Collection;
import java.util.Iterator;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

public class DocumentBinding
    extends AbstractBinding
    implements WSDL11ParameterBinding, Cloneable
{
    public DocumentBinding()
    {
        setStyle(SoapConstants.STYLE_DOCUMENT);
    }

    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        Collection operations = endpoint.getServiceInfo().getOperations();
        read(inMessage, context, operations);
    }

    public void writeMessage(OutMessage message, XMLStreamWriter writer, MessageContext context)
        throws XFireFault
    {
        OperationInfo op = context.getExchange().getOperation();
        Object[] values = (Object[]) message.getBody();
        int i = 0;
        
        MessageInfo msgInfo = null;
        if (isClientModeOn())
        {
            msgInfo = op.getInputMessage();
        }
        else
        {
            msgInfo = op.getOutputMessage();
        }
        
        for(Iterator itr = msgInfo.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo outParam = (MessagePartInfo) itr.next();
            
            getBindingProvider().writeParameter(outParam, writer, context, values[i]);
            i++;
        }
    }

    public void createInputParts(WSDLBuilder builder,
                                 Message req, 
                                 OperationInfo op)
    {
        writeParameters(builder, req, op.getInputMessage().getMessageParts());
    }

    public void createOutputParts(WSDLBuilder builder,
                                  Message req, 
                                  OperationInfo op)
    {
        writeParameters(builder, req, op.getOutputMessage().getMessageParts());
    }

    private void writeParameters(WSDLBuilder builder,
                                 Message message, 
                                 Collection params)
    {
        for (Iterator itr = params.iterator(); itr.hasNext();)
        {
            MessagePartInfo param = (MessagePartInfo) itr.next();
            
            builder.addNamespaceImport(builder.getService().getServiceInfo().getName().getNamespaceURI(), 
                                       param.getSchemaType().getSchemaType().getNamespaceURI());
            
            Part part = builder.createPart(param);
            
            message.addPart(part);
        }
    }

    public Object clone()
    {
        DocumentBinding binding = new DocumentBinding();
        binding.setBindingProvider(getBindingProvider());
        
        return binding;
    }
}
