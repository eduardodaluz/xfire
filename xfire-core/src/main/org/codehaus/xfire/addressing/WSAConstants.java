package org.codehaus.xfire.addressing;

/**
 * Constants for WS-Addressing.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface WSAConstants
{
    String WSA_NAMESPACE_200408 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    String WSA_NAMESPACE_200502 = "http://www.w3.org/2005/02/addressing";

    String WSA_PREFIX = "wsa";
    
    String WSA_ACTION = "Action";
    String WSA_ACTION_QNAME = WSA_PREFIX + ":" + WSA_ACTION;
    
    String WSA_TO = "To";
    String WSA_TO_QNAME = WSA_PREFIX + ":" + WSA_TO;

    String WSA_FAULT_TO = "FaultTo";
    String WSA_FAULT_TO_QNAME = WSA_PREFIX + ":" + WSA_FAULT_TO;
    
    String WSA_FROM = "From";
    String WSA_FROM_QNAME = WSA_PREFIX + ":" + WSA_FROM;
    
    String WSA_REPLY_TO = "ReplyTo";
    String WSA_REPLY_TO_QNAME = WSA_PREFIX + ":" + WSA_REPLY_TO;
    
    String WSA_RELATES_TO = "RelatesTo";
    String WSA_RELATES_TO_QNAME = WSA_PREFIX + ":" + WSA_RELATES_TO;
    
    String WSA_MESSAGE_ID = "MessageID";
    String WSA_MESSAGE_ID_QNAME = WSA_PREFIX + ":" + WSA_MESSAGE_ID;
    
    String WSA_RELATIONSHIP_TYPE = "RelationshipType";
    String WSA_RELATIONSHIP_TYPE_QNAME = WSA_PREFIX + ":" + WSA_RELATIONSHIP_TYPE;
    
    String WSA_REFERENCE_PROPERTIES = "ReferenceProperties";
    String WSA_REFERENCE_PROPERTIES_QNAME = WSA_PREFIX + ":" + WSA_REFERENCE_PROPERTIES;
    
    String WSA_REFERENCE_PARAMETERS = "ReferenceParameters";
    String WSA_REFERENCE_PARAMETERS_QNAME = WSA_PREFIX + ":" + WSA_REFERENCE_PARAMETERS;
    
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

    String WSA_NAMESPACE = WSA_NAMESPACE_200408;
}
