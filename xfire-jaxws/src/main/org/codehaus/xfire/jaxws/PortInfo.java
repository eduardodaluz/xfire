package org.codehaus.xfire.jaxws;

import javax.xml.namespace.QName;

public class PortInfo implements javax.xml.ws.handler.PortInfo
{
    private String bindingID;
    private QName portName;
    private QName serviceName;
    
    public PortInfo(String bindingID, QName portName, QName serviceName)
    {
        super();
        // TODO Auto-generated constructor stub
        this.bindingID = bindingID;
        this.portName = portName;
        this.serviceName = serviceName;
    }
    
    public String getBindingID()
    {
        return bindingID;
    }
    public QName getPortName()
    {
        return portName;
    }
    public QName getServiceName()
    {
        return serviceName;
    }
}
