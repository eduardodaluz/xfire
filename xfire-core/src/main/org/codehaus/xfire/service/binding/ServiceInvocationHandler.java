package org.codehaus.xfire.service.binding;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
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
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;
import org.jdom.Namespace;

public class ServiceInvocationHandler 
    extends AbstractHandler
{
    private static final Log logger = LogFactory.getLog(AbstractBinding.class.getName());
    
    public String getPhase()
    {
        return Phase.SERVICE;
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
            final List headerInfos = binding.getHeaders(operation.getInputMessage());
            for (Iterator itr = headerInfos.iterator(); itr.hasNext();)
            {
                MessagePartInfo header = (MessagePartInfo) itr.next();
                BindingProvider provider = context.getService().getBindingProvider();
                XMLStreamReader headerReader = getXMLStreamReader(context.getInMessage(), header);

                Object headerVal = null;
                if (headerReader != null)
                {
                    headerVal = provider.readParameter(header, headerReader, context);
                }
                
                params.add(header.getIndex(), headerVal);
            }

            final Invoker invoker = context.getService().getInvoker();
            
            // invoke the service method...
            if (!operation.isAsync())
            {
                sendMessage(context, params, operation, invoker);
            }
            else
            {
                Runnable runnable = new Runnable() 
                {
                    public void run() 
                    {
                        try
                        {
                            sendMessage(context, params, operation, invoker);
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

    protected void sendMessage(final MessageContext context,
                               final List params,
                               final OperationInfo operation,
                               final Invoker invoker)
        throws Exception
    {
        final Object value = invoker.invoke(operation.getMethod(),
                                            params.toArray(),
                                            context);

        if (context.getExchange().hasOutMessage())
        {
            OutMessage outMsg = context.getExchange().getOutMessage();
            outMsg.setBody(new Object[] {value});
            outMsg.setSerializer(context.getBinding().getSerializer(operation));
            context.getOutPipeline().invoke(context);
        }
    }
    
    private XMLStreamReader getXMLStreamReader(AbstractMessage msg, MessagePartInfo header)
    {
        QName name = header.getName();
        Element el = 
            msg.getHeader().getChild(name.getLocalPart(), Namespace.getNamespace(name.getNamespaceURI()));
        
        if (el == null) return null;
        
        return new JDOMStreamReader(el);
    }

}