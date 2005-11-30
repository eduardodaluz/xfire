package org.codehaus.xfire.addressing;

import org.jdom.Namespace;

public class AddressingHeadersFactory200508
    extends AbstactAddressingHeadersFactory2005
{

  
    public String getAnonymousUri()
    {
        return WSA_200408_ANONYMOUS_URI;
    }

    protected Namespace getNamespace()
    {
        return Namespace.getNamespace(WSA_PREFIX, WSA_NAMESPACE_200508);
    }

    public String getNoneUri()
    {
        return "http://www.w3.org/2005/08/addressing/none";
    }

  

}
