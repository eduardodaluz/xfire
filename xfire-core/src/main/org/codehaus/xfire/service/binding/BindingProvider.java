package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.EndpointHandler;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.SchemaType;

public interface BindingProvider
{
    void initialize(Service newParam);
    
    Object readParameter(MessagePartInfo p, MessageContext context) throws XFireFault;
    
    void writeParameter(MessagePartInfo p, MessageContext context, Object value) throws XFireFault;

    SchemaType getSchemaType(Service service, MessagePartInfo param);
    
    EndpointHandler createEndpointHandler();
}
