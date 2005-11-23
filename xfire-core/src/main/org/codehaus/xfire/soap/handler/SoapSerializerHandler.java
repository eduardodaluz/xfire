package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.soap.SoapSerializer;

public class SoapSerializerHandler
    extends AbstractHandler
{
    public String getPhase()
    {
        return Phase.TRANSPORT;
    }

    /**
     * Validates that the mustUnderstand and role headers are processed correctly.
     *
     * @param context
     * @throws XFireFault
     */
    public void invoke(MessageContext context)
        throws Exception
    {
        MessageSerializer serializer = context.getOutMessage().getSerializer();
        if (serializer == null)
        {
            SoapBinding binding = (SoapBinding) context.getBinding();
            if (binding == null)
            {
                throw new XFireException("Couldn't find the binding!");
            }
            serializer = SoapBindingHandler.getSerializer(binding.getStyle(), binding.getUse());
        }
        
        context.getOutMessage().setSerializer(new SoapSerializer(serializer));
    }
}
