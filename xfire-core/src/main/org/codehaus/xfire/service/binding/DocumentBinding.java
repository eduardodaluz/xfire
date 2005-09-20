package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
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
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.wsdl11.WSDL11ParameterBinding;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

public class DocumentBinding
    extends AbstractBinding
    implements WSDL11ParameterBinding, Cloneable
{
    public DocumentBinding()
    {
        setStyle(SoapConstants.STYLE_DOCUMENT);
        setUse(SoapConstants.USE_LITERAL);
    }

    public void readMessage(InMessage inMessage, MessageContext context)
        throws XFireFault
    {
        Service endpoint = context.getService();
        
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(context.getInMessage().getXMLStreamReader());
        
        while (STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = findMessagePart(endpoint, dr.getName());
            
            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getBindingProvider().readParameter(p, dr, context) );
        }

        if (!isClientModeOn())
        {
            setOperation(findOperation(endpoint, parameters), context);
        }
        
        context.getInMessage().setBody(parameters);
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

    protected OperationInfo findOperation(Service endpoint, List parameters)
    {
        for ( Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo o = (OperationInfo) itr.next();
            List messageParts = o.getInputMessage().getMessageParts();
            if ( messageParts.size() == parameters.size() )
            {
                if (checkParameters(messageParts, parameters))
                    return o;
            }
        }
        
        return null;
    }

    private boolean checkParameters(List messageParts, List parameters)
    {
        Iterator messagePartIterator = messageParts.iterator();
        for (Iterator parameterIterator = parameters.iterator(); parameterIterator.hasNext();)
        {
            Object param = parameterIterator.next();
            MessagePartInfo mpi = (MessagePartInfo) messagePartIterator.next();
            if (!mpi.getTypeClass().isAssignableFrom(param.getClass()))
                return false;
        }
        return true;
    }

    protected MessagePartInfo findMessagePart(Service endpoint, QName name)
    {

        for ( Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo op = (OperationInfo) itr.next();
            MessageInfo msgInfo = null;
            if (isClientModeOn())
            {
                msgInfo = op.getOutputMessage();
            }
            else
            {
                msgInfo = op.getInputMessage();
            }

            MessagePartInfo p = msgInfo.getMessagePart(name);

            if ( p != null )
                return p;
        }
        return null;
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
