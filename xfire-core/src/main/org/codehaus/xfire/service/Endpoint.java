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
        if (name == null) throw new IllegalStateException("Endpoint name cannot be null.");
        if (binding == null) throw new IllegalStateException("Binding cannot be null.");
        
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
