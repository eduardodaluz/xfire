package org.codehaus.xfire.addressing;


import org.jdom.Namespace;

/**
 * A WS-Addressing endpoint reference.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AddressingHeadersFactory200502
    extends AbstactAddressingHeadersFactory2005
    implements WSAConstants, AddressingHeadersFactory
{
    
    public String getAnonymousUri()
    {
        return "http://www.w3.org/2005/02/addressing/role/anonymous";
    }

    protected Namespace getNamespace()
    {
        return Namespace.getNamespace(WSA_PREFIX, WSA_NAMESPACE_200502);
    }

    public String getNoneUri()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
