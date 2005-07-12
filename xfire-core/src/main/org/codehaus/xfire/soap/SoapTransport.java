package org.codehaus.xfire.soap;

import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.soap.handler.FaultSoapSerializerHandler;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;
import org.codehaus.xfire.soap.handler.SoapSerializerHandler;
import org.codehaus.xfire.soap.handler.ValidateHeadersHandler;
import org.codehaus.xfire.transport.AbstractTransport;
import org.codehaus.xfire.transport.Transport;

/**
 * Provides soap messaging support to a channel by adding the SOAP handlers.
 * 
 * @see org.codehaus.xfire.soap.handler.ReadHeadersHandler
 * @see org.codehaus.xfire.soap.handler.ValidateHeadersHandler
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class SoapTransport
{
    private static final Handler readHeaders = new ReadHeadersHandler();
    private static final Handler validate = new ValidateHeadersHandler();
    private static final Handler serializer = new SoapSerializerHandler();
    private static final Handler faultSerializer = new FaultSoapSerializerHandler();
    
    public static Transport createSoapTransport(AbstractTransport transport)
    {
        transport.addInHandler(readHeaders);
        transport.addInHandler(validate);
        
        transport.addOutHandler(serializer);
        transport.addFaultHandler(faultSerializer);
        
        return transport;
    }
}
