package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.handler.Handler;

/**
 * Binds a SOAP Body and its Headers to Java objects. It will (de)Serialize any wrapper 
 * elements before passing the stream off to a BindingProvider.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ObjectBinding
    extends Handler, MessageSerializer
{
    Invoker getInvoker();
    
    void setInvoker(Invoker invoker);

    BindingProvider getBindingProvider();
    
    void setBindingProvider(BindingProvider bindingProvider);
}