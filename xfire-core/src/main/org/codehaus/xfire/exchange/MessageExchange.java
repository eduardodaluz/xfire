package org.codehaus.xfire.exchange;

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

    InMessage getInMessage();
    void setInMessage(InMessage inMessage);
    boolean hasInMessage();
    
    OutMessage getOutMessage();
    void setOutMessage(OutMessage outMessage);
    boolean hasOutMessage();
    
    AbstractMessage getFaultMessage();
    void setFaultMessage(AbstractMessage faultMessage);
    boolean hasFaultMessage();
    
    AbstractMessage getMessage(String type);
    void setMessage(String type, AbstractMessage faultMessage);
    boolean hasMessage(String type);
}
