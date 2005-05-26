package org.codehaus.xfire.addressing;

/**
 * WS-Addressing Headers from a SOAP message.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface AddressingHeaders
{
    String getMessageID();
    
    String getAction();

    String getTo();
    
    EndpointReference getFrom();
    
    EndpointReference getReplyTo();
    
    EndpointReference getFaultTo();
}
