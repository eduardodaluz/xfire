package org.codehaus.xfire.soap.handler;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * Inspects the SOAPAction if there is one, and selects the appropriate Operation.
 * 
 * @author Dan Diephouse
 */
public class SoapActionInHandler
    extends AbstractHandler
{
    public SoapActionInHandler()
    {
        super();

        before(ReadHeadersHandler.class.getName());
    }

    public String getPhase()
    {
        return Phase.DISPATCH;
    }

    public void invoke(MessageContext context)
        throws Exception
    {
        Service service = context.getService();
        if (service == null) return;
        
        String action = (String) context.getInMessage().getProperty(SoapConstants.SOAP_ACTION);
        
        if (action == null || action.length() == 0) return;
        
        SoapBinding binding = (SoapBinding) context.getBinding();
        OperationInfo op = binding.getOperationByAction(action);
        
        if (op != null) 
        {
            context.getExchange().setOperation(op);
        }
    }
}