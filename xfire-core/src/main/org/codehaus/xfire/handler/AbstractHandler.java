package org.codehaus.xfire.handler;

import javax.xml.namespace.QName;

import org.codehaus.xfire.AbstractXFireComponent;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public abstract class AbstractHandler
    extends AbstractXFireComponent
    implements Handler
{
	/**
     * Returns null by default, indicating that no headers
     * were understood.
     * 
     * @see org.codehaus.xfire.handler.Handler#getUnderstoodHeaders()
     */
    public QName[] getUnderstoodHeaders()
    {
        return null;
    }

    public String[] getRoles()
    {
        return null;
    }
    
    /**
     * @see org.codehaus.xfire.handler.Handler#handleFault(java.lang.Exception, org.codehaus.xfire.MessageContext)
     * @param e
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context)
    {
    }
}
