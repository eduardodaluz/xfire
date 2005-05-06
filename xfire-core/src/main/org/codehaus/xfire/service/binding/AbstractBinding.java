package org.codehaus.xfire.service.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.service.OperationInfo;

public abstract class AbstractBinding
    extends AbstractHandler
    implements ObjectBinding
{
    private static final Log logger = LogFactory.getLog(AbstractBinding.class.getName());

    public static final String OPERATION_KEY = "xfire.operation";
    
    public static final String RESPONSE_VALUE = "xfire.java.response";

    public static final String RESPONSE_PIPE = "xfire.java.responsePipe";

    private String style;
    private String use;
    private Invoker invoker;
    private BindingProvider bindingProvider;
    
    public abstract Object[] read(MessageContext context) throws XFireFault;
    public abstract void write(Object[] values, MessageContext context) throws XFireFault;;

    public OperationInfo getOperation(MessageContext context)
    {
        return (OperationInfo) context.getProperty(OPERATION_KEY);
    }

    public void invoke(final MessageContext context)
        throws Exception
    {
        try
        {
            // Read in the parameters...
            final Object[] params = read(context);

            // Don't read the operation in until after reading. Otherwise
            // it won't work for document style services.
            final OperationInfo operation = getOperation(context);

            final Invoker invoker = getInvoker();
            
            // invoke the service method...
            if (!operation.isOneWay())
            {
                final Object value = invoker.invoke(operation.getMethod(), params, context);

                context.setProperty(RESPONSE_VALUE, value);
            }
            else
            {
                Runnable runnable = new Runnable() 
                {
                    public void run() 
                    {
                        try
                        {
                            invoker.invoke(operation.getMethod(), params, context);
                        }
                        catch (XFireFault e)
                        {
                            context.getService().getFaultHandler().handleFault(e, context);
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

    public void writeResponse(final MessageContext context)
        throws XFireFault
    {
        final Object value = context.getProperty(RESPONSE_VALUE);
            
        write(new Object[] { value }, context);
    }

    public boolean hasResponse(MessageContext context)
    {
        return !getOperation(context).isOneWay();
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
}
