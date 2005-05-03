package org.codehaus.xfire.service.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.util.DepthXMLStreamReader;
import org.codehaus.xfire.util.STAXUtils;

/**
 * Bridges Document/Literal style messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public class DocumentBridge
    extends AbstractMessageBridge
{
	public DocumentBridge(MessageContext context)
    {
        super(context);
	}

    public List read()
    	throws XFireFault
    {
        List parameters = new ArrayList();
        DepthXMLStreamReader dr = new DepthXMLStreamReader(getRequestReader());

        while (STAXUtils.toNextElement(dr))
        {
            MessagePartInfo p = findParameter(dr.getName());
            
            if (p == null)
            {
                throw new XFireFault("Parameter " + dr.getName() + " does not exist!", 
                                     XFireFault.SENDER);
            }

            parameters.add( getService().getBindingProvider().readParameter(p, getContext()) );
        }

        setOperation( findOperation( parameters.size() ) );
        
        return parameters;
    }

    protected MessagePartInfo findParameter(QName name)
    {
        for ( Iterator itr = getService().getService().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo op = (OperationInfo) itr.next();
            MessagePartInfo p = op.getInputMessage().getMessagePart(name);
            
            if ( p != null )
                return p;
        }
        return null;
    }

    protected OperationInfo findOperation(int i)
    {
        for ( Iterator itr = getService().getService().getOperations().iterator(); itr.hasNext(); )
        {
            OperationInfo o = (OperationInfo) itr.next();
            if ( o.getInputMessage().getMessageParts().size() == i )
                return o;
        }
        
        return null;
    }

    public void write(Object[] values)
    	throws XFireFault
    {
        int i = 0;
        for(Iterator itr = getOperation().getOutputMessage().getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo outParam = (MessagePartInfo) itr.next();
            
            getService().getBindingProvider().writeParameter(outParam, getContext(), values[i]);
            i++;
        }
    }
}