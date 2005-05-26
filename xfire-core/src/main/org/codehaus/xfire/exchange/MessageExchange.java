package org.codehaus.xfire.exchange;

import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.OperationInfo;

/**
 * A MessageExchange encapsulates the orchestration of a message
 * exchange pattern.  This makes it easy to handle various interactions -
 * like robust in-out, robust in, in, out, WS-Addressing MEPs, etc.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface MessageExchange
{
    void setOperation(OperationInfo operation);
    
    OperationInfo getOperation();
    
    void doExchange();

    void handleFault(XFireFault fault);
}
