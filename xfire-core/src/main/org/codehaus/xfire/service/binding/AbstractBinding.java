package org.codehaus.xfire.service.binding;

import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.MessageExchange;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.service.MessageHeaderInfo;
import org.codehaus.xfire.service.OperationInfo;

public abstract class AbstractBinding
    extends AbstractHandler
    implements ObjectBinding, Cloneable
{
    private static final Log logger = LogFactory.getLog(AbstractBinding.class.getName());

    public static final String OPERATION_KEY = "xfire.operation";
    
    public static final String RESPONSE_VALUE = "xfire.java.response";

    public static final String RESPONSE_PIPE = "xfire.java.responsePipe";

    private String style;
    private String use;
    private Invoker invoker;
    private BindingProvider bindingProvider;
    private boolean clientModeOn = false;
    
    public void setOperation(OperationInfo operation, MessageContext context)
    {
        MessageExchange exchange = context.createMessageExchange(operation);
        
        context.setExchange(exchange);
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
            final List headerInfos = operation.getInputMessage().getMessageHeaders();
            for (Iterator itr = headerInfos.iterator(); itr.hasNext();)
            {
                MessageHeaderInfo header = (MessageHeaderInfo) itr.next();
                BindingProvider provider = context.getService().getBinding().getBindingProvider();
                params.add(header.getIndex(), provider.readHeader(header, context));
            }

            final Invoker invoker = getInvoker();
            
            // invoke the service method...
            if (!operation.isAsync())
            {
                final Object value = invoker.invoke(operation.getMethod(),
                                                    params.toArray(),
                                                    context);

                OutMessage outMsg = context.getOutMessage();
                if (outMsg != null)
                {
                    outMsg.setBody(new Object[] {value});
                    context.setOutMessage(outMsg);
                }
            }
            else
            {
                Runnable runnable = new Runnable() 
                {
                    public void run() 
                    {
                        try
                        {
                            final Object value = invoker.invoke(operation.getMethod(), 
                                                                params.toArray(), 
                                                                context);

                            OutMessage outMsg = context.getOutMessage();
                            if (outMsg != null)
                            {
                                outMsg.setBody(new Object[] {value});
                                context.setOutMessage(outMsg);
                            }
                        }
                        catch (XFireFault e)
                        {
                            context.getExchange().handleFault(e);
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
            throw new XFireFault("Error invoking service.", e, XFireFault.SENDER);
        }
    }

    protected void nextEvent(XMLStreamReader dr)
    {
        try
        {
            dr.next();
        }
        catch (XMLStreamException e)
        {
            throw new XFireRuntimeException("Couldn't parse stream.", e);
        }
    }

    public Invoker getInvoker()
    {
        return invoker;
    }
 
    public void setInvoker(Invoker invoker)
    {
        this.invoker = invoker;
    }
    
    public String getStyle()
    {
        return style;
    }
    
    protected void setStyle(String style)
    {
        this.style = style;
    }
    
    public String getUse()
    {
        return use;
    }
    
    protected void setUse(String use)
    {
        this.use = use;
    }

    public BindingProvider getBindingProvider()
    {
        return bindingProvider;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public boolean isClientModeOn()
    {
        return clientModeOn;
    }

    public void setClientModeOn(boolean clientModeOn)
    {
        this.clientModeOn = clientModeOn;
    }
    
    public abstract Object clone();
}
