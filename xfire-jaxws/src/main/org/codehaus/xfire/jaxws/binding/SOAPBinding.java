package org.codehaus.xfire.jaxws.binding;

import java.util.Set;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;

import org.codehaus.xfire.transport.Transport;

public class SOAPBinding extends AbstractBinding implements javax.xml.ws.soap.SOAPBinding
{
    private boolean mtomEnabled = false;
    private Set<String> roles;
    
    public SOAPBinding(Transport t)
    {
        super(t);
    }

    public MessageFactory getMessageFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SOAPFactory getSOAPFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isMTOMEnabled()
    {
        return mtomEnabled;
    }

    public void setMTOMEnabled(boolean mtomEnabled)
    {
        this.mtomEnabled = mtomEnabled;
    }

    public Set<String> getRoles() 
    {
        return roles;
    }

    public void setRoles(Set<String> roles) 
    {
        this.roles = roles;
    }
}
