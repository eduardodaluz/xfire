package org.codehaus.xfire.service;

import javax.xml.namespace.QName;

public class Endpoint extends Extensible
{
    private QName name;
    private Binding binding;
    private String address;

    public Endpoint(QName name, Binding binding)
    {
        this.binding = binding;
        this.name = name;
    }
    
    public Endpoint(QName name, Binding binding, String address)
    {
        this.address = address;
        this.binding = binding;
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

    public Binding getBinding()
    {
        return binding;
    }
}
