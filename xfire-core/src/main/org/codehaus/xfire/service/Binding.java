package org.codehaus.xfire.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

public abstract class Binding
    extends Extensible
{
    private QName name;

    private String bindingId;

    private Service service;

    private Transport transport;

    private Set headers = new HashSet();

    protected Binding(QName name, String bindingId, Service service)
    {
        this.name = name;
        this.bindingId = bindingId;
        this.service = service;
    }

    public QName getName()
    {
        return name;
    }

    public Service getService()
    {
        return service;
    }

    public String getBindingId()
    {
        return bindingId;
    }

    public Transport getTransport()
    {
        return transport;
    }

    public void setTransport(Transport transport)
    {
        this.transport = transport;
    }

    public abstract javax.wsdl.Binding createBinding(WSDLBuilder builder, PortType portType);

    public abstract Port createPort(WSDLBuilder builder, javax.wsdl.Binding wbinding);

    public abstract Port createPort(Endpoint endpoint,
                                    WSDLBuilder builder,
                                    javax.wsdl.Binding wbinding);

    public void setHeader(MessagePartInfo part, boolean b)
    {
        if (b) headers.add(part);
        else headers.remove(part);
    }

    public Collection getHeaders()
    {
        return Collections.unmodifiableSet(headers);
    }

    public boolean isHeader(MessagePartInfo part)
    {
        return headers.contains(part);
    }

    public List getHeaders(MessageInfo msg)
    {
        List inputHeaders = new ArrayList();
        for (Iterator itr = msg.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
            if (isHeader(part)) 
                inputHeaders.add(part);
        }
        return inputHeaders;
    }
}
