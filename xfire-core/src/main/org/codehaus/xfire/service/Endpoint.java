package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

public class Endpoint
{
    private QName name;
    private String bindingId;
    private String address;
 
    public Endpoint(QName name, String id, String address)
    {
        this.address = address;
        this.bindingId = id;
        this.name = name;
    }
    
    public QName getName()
    {
        return name;
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public String getBindingId()
    {
        return bindingId;
    }
}
