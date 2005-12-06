package org.codehaus.xfire.client;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;

/**
 * Takes an XFireFault and converts it to a local exception type if possible.
 * 
 * @author Dan Diephouse
 */
public class ClientFaultConverter extends AbstractHandler
{
    public void invoke(MessageContext context)
        throws Exception
    {
        XFireFault fault = (XFireFault) context.getExchange().getFaultMessage().getBody();
        
        Element detail = fault.getDetail();
        if (detail != null)
        {
            processFaultDetail(context, detail);
        }
    }

    protected void processFaultDetail(MessageContext context, Element detail)
        throws Exception
    {
        if (detail.getContentSize() == 0)
            return;
        
        Element exDetail = (Element) detail.getContent().get(0);
        
        MessagePartInfo faultPart = getFaultPart(context.getExchange().getOperation(),
                                                 exDetail);

        if (faultPart == null)
            return;

        BindingProvider provider = context.getService().getBindingProvider();
        JDOMStreamReader reader = new JDOMStreamReader(exDetail);
        reader.nextTag();
        
        Exception e = (Exception) provider.readParameter(faultPart, reader, context);
        
        context.getExchange().getFaultMessage().setBody(e);
    }
    
    protected MessagePartInfo getFaultPart(OperationInfo operation, Element exDetail)
    {
        QName qname = new QName(exDetail.getNamespaceURI(), exDetail.getName());
        
        for (Iterator itr = operation.getFaults().iterator(); itr.hasNext();)
        {
            FaultInfo faultInfo = (FaultInfo) itr.next();
            
            MessagePartInfo part = faultInfo.getMessagePart(qname);
            
            if (part != null) return part;
        }
        
        return null;
    }
    
}
