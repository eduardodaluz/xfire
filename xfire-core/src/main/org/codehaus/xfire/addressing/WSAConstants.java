package org.codehaus.xfire.addressing;

/**
 * Constants for WS-Addressing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSAConstants
{
    String WSA_NAMESPACE_200412 = "http://www.w3.org/2004/12/addressing";
    String WSA_NAMESPACE_200502 = "http://www.w3.org/2005/02/addressing";

    String WSA_PREFIX = "wsa";
    
    String WSA_ACTION = "Action";
    String WSA_TO = "To";
    String WSA_FROM = "From";
    String WSA_REPLY_TO = "ReplyTo";
    String WSA_MESSAGE_ID = "MessageID";

    String WSA_ADDRESS = "Address";
    String WSA_ADDRESS_QNAME = WSA_PREFIX + ":" + WSA_ADDRESS;
    
    String WSA_INTERFACE_NAME = "InterfaceName";
    String WSA_INTERFACE_NAME_QNAME = WSA_PREFIX + ":" + WSA_INTERFACE_NAME;
    
    String WSA_SERVICE_NAME = "ServiceName";
    String WSA_SERVICE_NAME_QNAME = WSA_PREFIX + ":" + WSA_SERVICE_NAME;
    
    String WSA_ENDPOINT_NAME = "EndpointName";
    String WSA_ENDPOINT_NAME_QNAME = WSA_ENDPOINT_NAME;

    String WSA_POLICIES = "Policies";
    String WSA_POLICIES_QNAME = WSA_PREFIX + ":" + WSA_POLICIES;

    String WSA_NAMESPACE = WSA_NAMESPACE_200412;   
    
}
