package org.codehaus.xfire.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public abstract class AbstractHandler
    implements Handler
{
    private List before = new ArrayList();
    private List after = new ArrayList();
    
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
    
    public String getPhase()
    {
        return Phase.USER;
    }
    
    /**
     * @see org.codehaus.xfire.handler.Handler#handleFault(java.lang.Exception, org.codehaus.xfire.MessageContext)
     * @param e
     * @param context
     */
    public void handleFault(XFireFault fault, MessageContext context)
    {
    }
    
    public void after(String handler)
    {
        after.add(handler);
    }
    
    public void before(String handler)
    {
        before.add(handler);
    }

    public Collection getAfter()
    {
        return after;
    }

    public Collection getBefore()
    {
        return before;
    }

    public int compareTo(Object o1)
    {
        Handler h1 = (Handler) o1;
        if (h1 == this) return 0;
        
        boolean thisBefore = getBefore().contains(h1.getClass().getName());
        boolean thisAfter = getAfter().contains(h1.getClass().getName());
        boolean thisAfter2 = h1.getBefore().contains(getClass().getName());
        boolean thisBefore2 = h1.getAfter().contains(getClass().getName());

        if ((thisBefore2 && thisAfter) || (thisBefore && thisAfter2))
            throw new XFireRuntimeException("Impossible ordering!");
        
        if ( thisAfter2 || thisAfter) return 1;
        else if (thisBefore2  || thisBefore) return -1;
        else return 0;
    }
}
