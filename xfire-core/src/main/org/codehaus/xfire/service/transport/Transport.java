package org.codehaus.xfire.service.transport;

import org.codehaus.xfire.MessageContext;


/**
 * Transport
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public interface Transport
{
    String getAddress();

    String getTransportURI();

    MessageContext createMessageContext();


}
