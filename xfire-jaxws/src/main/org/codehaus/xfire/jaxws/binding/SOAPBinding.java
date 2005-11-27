package org.codehaus.xfire.jaxws.binding;

import java.net.URI;
import java.util.Set;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;

import org.codehaus.xfire.transport.Transport;

public class SOAPBinding extends AbstractBinding implements javax.xml.ws.soap.SOAPBinding
{
    private boolean mtomEnabled = false;
    
    public SOAPBinding(Transport t)
    {
        super(t);
    }

    public MessageFactory getMessageFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<URI> getRoles()
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

    public void setRoles(Set<URI> arg0)
    {
    }
}
