package org.codehaus.xfire.service.bridge;
import java.util.List;

import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.binding.Operation;

/**
 * Reads/Writes Messages.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Apr 7, 2004
 */
public interface MessageBridge
{
	List read() throws XFireFault;

    void write( Object[] values ) throws XFireFault;

    /**
     * @return The operation that is being invoked in this request.
     */
    Operation getOperation();
}