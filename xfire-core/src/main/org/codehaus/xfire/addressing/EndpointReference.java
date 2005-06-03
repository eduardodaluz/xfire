package org.codehaus.xfire.addressing;

import java.util.List;

import javax.xml.namespace.QName;

public class EndpointReference
{
    private String address;

    private QName interfaceName;

    private QName serviceName;

    private String endpointName;

    private List policies;

    private List any;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public List getAny()
    {
        return any;
    }

    public void setAny(List any)
    {
        this.any = any;
    }

    public String getEndpointName()
    {
        return endpointName;
    }

    public void setEndpointName(String endpointName)
    {
        this.endpointName = endpointName;
    }

    public QName getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName(QName interfaceName)
    {
        this.interfaceName = interfaceName;
    }

    public List getPolicies()
    {
        return policies;
    }

    public void setPolicies(List policies)
    {
        this.policies = policies;
    }

    public QName getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(QName serviceName)
    {
        this.serviceName = serviceName;
    }

}