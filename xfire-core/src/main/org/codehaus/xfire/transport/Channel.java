package org.codehaus.xfire.transport;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.fault.XFireFault;

/**
 * A channel for communication. This can be a channel on an underlying transport -
 * like HTTP - or wrap another channel and provide additional functions - like
 * reliable messaging.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Channel
{
    /** 
     * The URI which represents that a message should be sent over a back channel, i.e.
     * an HttpServletResponse, instead of opening a new connection.
     */
    String BACKCHANNEL_URI = "urn:xfire:channel:backchannel";

    void open();
    
    void send(MessageContext context, OutMessage message) throws XFireFault;
    
    void receive(MessageContext context, InMessage message);

    void setEndpoint(ChannelEndpoint receiver);

    void close();
    
    Transport getTransport();

    String getUri();
}
