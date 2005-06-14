package org.codehaus.xfire.service.binding;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.Service;

public interface BindingProvider
{
    void initialize(Service newParam);

    Object readParameter(MessagePartInfo p, XMLStreamReader reader, MessageContext context)
        throws XFireFault;

    void writeParameter(MessagePartInfo p,
                        XMLStreamWriter writer,
                        MessageContext context,
                        Object value)
        throws XFireFault;
}
