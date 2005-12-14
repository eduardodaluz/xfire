package org.codehaus.xfire.spring.config;

import java.util.List;

import javax.xml.namespace.QName;

public abstract class AbstractSoapBindingBean
{
    private String transport;
    private List endpoints;
    private QName name;
    private boolean allowUndefinedEndpoints;
    
    public boolean isAllowUndefinedEndpoints()
    {
        return allowUndefinedEndpoints;
    }

    public void setAllowUndefinedEndpoints(boolean allowUndefinedEndpoints)
    {
        this.allowUndefinedEndpoints = allowUndefinedEndpoints;
    }

    public QName getName()
    {
        return name;
    }

    public void setName(QName name)
    {
        this.name = name;
    }

    public String getTransport()
    {
        return transport;
    }

    public void setTransport(String transport)
    {
        this.transport = transport;
    }

    public List getEndpoints()
    {
        return endpoints;
    }

    public void setEndpoints(List endpoints)
    {
        this.endpoints = endpoints;
    }
}
