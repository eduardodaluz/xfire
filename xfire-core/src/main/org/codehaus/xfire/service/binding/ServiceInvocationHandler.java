package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.AbstractMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.util.stax.ElementStreamWriter;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;
import org.jdom.Namespace;

public class ServiceInvocationHandler 
    extends AbstractHandler
{
    private static final Log logger = LogFactory.getLog(AbstractBinding.class.getName());
    
    public ServiceInvocationHandler() 
    {
        super();
        setPhase(Phase.SERVICE);
    }

    public void invoke(final MessageContext context)
        throws Exception
    {
        try
        {
            // Read in the parameters...
            final List params = (List) context.getInMessage().getBody();

            // Don't read the operation in until after reading. Otherwise
            // it won't work for document style services.
            final OperationInfo operation = context.getExchange().getOperation();

            // read in the headers
            Binding binding = context.getBinding();
            
            MessageInfo msg = AbstractBinding.getIncomingMessageInfo(context);
            MessageInfo outMsg = AbstractBinding.getOutgoingMessageInfo(context);
            MessagePartContainer headerMsg = binding.getHeaders(msg);
            MessagePartContainer outHeaderMsg = null;
            if (outMsg != null) outHeaderMsg = binding.getHeaders(outMsg);
            
            final Object[] paramArray = fillInHolders(context, operation, msg, outMsg, headerMsg, outHeaderMsg, params);
            context.getInMessage().setBody(paramArray);
            
            readHeaders(context, headerMsg, paramArray);

            final Invoker invoker = context.getService().getInvoker();
            
            // invoke the service method...
            if (!operation.isAsync())
            {
                sendMessage(context, paramArray, operation, invoker);
            }
            else
            {
                Runnable runnable = new Runnable() 
                {
                    public void run() 
                    {
                        try
                        {
                            sendMessage(context, paramArray, operation, invoker);
                        }
                        catch (Exception e)
                        {
                            XFireFault fault = XFireFault.createFault(e);
                            
                            context.getInPipeline().handleFault(fault, context);
                        }
                    }
                };
                
                Thread opthread = new Thread(runnable);
                opthread.start();
            }
        }
        catch (XFireRuntimeException e)
        {
            logger.warn("Error invoking service.", e);
            throw new XFireFault("Error invoking service" + (e.getMessage() != null ? ": " + e.getMessage() : ".") , e, XFireFault.SENDER);
        }
    }

    public static void readHeaders(final MessageContext context, 
                                   MessagePartContainer headerMsg, 
                                   final Object[] paramArray)
        throws XFireFault
    {
        final List headerInfos = headerMsg.getMessageParts();
        for (Iterator itr = headerInfos.iterator(); itr.hasNext();)
        {
            MessagePartInfo header = (MessagePartInfo) itr.next();

            BindingProvider provider = context.getService().getBindingProvider();

            XMLStreamReader headerReader = getXMLStreamReader(context.getInMessage(), header);
            // check to see if there is a header to read
            if (headerReader == null) continue;
            
            Object headerVal = provider.readParameter(header, headerReader, context);

            // why the null check? In case there is a Holder class of some sort there.
            if (paramArray[header.getIndex()] == null)
            {
                paramArray[header.getIndex()] = headerVal;
            }
        }
    }

    /**
     * Looks for holders, instantiates them, then inserts them into the parameters.
     * @return
     */
    protected Object[] fillInHolders(MessageContext context,
                                     OperationInfo opInfo, 
                                     MessageInfo inMsg, 
                                     MessageInfo outMsg, 
                                     MessagePartContainer headerMsg, 
                                     MessagePartContainer outHeaderMsg, 
                                     List params)
    {
        // Gross hack to calculate the size of the method parameters, minus special
        // parameters like MessageContext.
        int outSize = 0;
        if (outMsg != null)
        {
            outSize = outHeaderMsg.size() + ((outMsg.size() == 0) ? 0 : outMsg.size() - 1);
        }
        
        int total = inMsg.size() + headerMsg.size() + outSize;
        
        // there are no holder classes to fill in
        if (total == params.size()) return params.toArray();
        
        Object[] newParams = new Object[total];
        List parts = inMsg.getMessageParts();
        for (int i = 0; i < parts.size(); i++)
        {
            MessagePartInfo part = (MessagePartInfo) parts.get(i);
            newParams[part.getIndex()] = params.get(i);
        }
        
        // Case for filling in holders - in server mode
        if (!AbstractBinding.isClientModeOn(context))
        {
            fillInHolders(outMsg, newParams);
            fillInHolders(outHeaderMsg, newParams);
        }
        
        return newParams;
    }

    private void fillInHolders(MessagePartContainer msg, Object[] newParams)
    {
        for (Iterator itr = msg.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
 
            if (part.getIndex() >= 0)
            {
                try
                {
                    Object holder = part.getTypeClass().newInstance();
                    newParams[part.getIndex()] = holder;
                }
                catch (Exception e)
                {
                    throw new XFireRuntimeException("Could not instantiate holder class.", e);
                }
            }  
        }
    }
    
    protected void sendMessage(final MessageContext context,
                               final Object[] params,
                               final OperationInfo operation,
                               final Invoker invoker)
        throws Exception
    {
        final Object value = invoker.invoke(operation.getMethod(),
                                            params,
                                            context);

        if (context.getExchange().hasOutMessage())
        {
            OutMessage outMsg = context.getExchange().getOutMessage();
            writeHeaders(context);
            context.setCurrentMessage(outMsg);
            outMsg.setBody(new Object[] {value});
            outMsg.setSerializer(context.getBinding().getSerializer(operation));
            context.getOutPipeline().invoke(context);
        }
    }
    
    public static void writeHeaders(MessageContext context) throws XFireFault, XMLStreamException
    {
        MessageInfo msgInfo = AbstractBinding.getOutgoingMessageInfo(context);
        MessagePartContainer headers = context.getBinding().getHeaders(msgInfo);
        Object[] body = (Object[]) context.getCurrentMessage().getBody();

        ElementStreamWriter writer = new ElementStreamWriter(context.getOutMessage().getHeader());
        for (Iterator itr = headers.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
            BindingProvider provider = context.getService().getBindingProvider();

            AbstractBinding.writeParameter(writer,
                                           context,
                                           body[part.getIndex()],
                                           part,
                                           part.getName().getNamespaceURI());
        }
    }

    private static XMLStreamReader getXMLStreamReader(AbstractMessage msg, MessagePartInfo header)
    {
        QName name = header.getName();
        Element el = 
            msg.getHeader().getChild(name.getLocalPart(), Namespace.getNamespace(name.getNamespaceURI()));
        
        if (el == null) return null;
        
        JDOMStreamReader reader = new JDOMStreamReader(el);
        
        try
        {
            // position at start_element
            reader.next();
        }
        catch (XMLStreamException e)
        {
        }
        
        return reader;
    }

}
