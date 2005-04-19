package org.codehaus.xfire.service.binding;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.SchemaType;

public interface BindingProvider
{
    void initialize(Service newParam);
    
    Object readParameter(Parameter p, MessageContext context) throws XFireFault;
    
    void writeParameter(Parameter p, MessageContext context, Object value) throws XFireFault;

    SchemaType getSchemaType(Service service, Parameter param);
}
