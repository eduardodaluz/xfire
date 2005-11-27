package org.codehaus.xfire.jaxws.binding;

import org.codehaus.xfire.transport.Transport;

public class HTTPBinding
    extends AbstractBinding
    implements javax.xml.ws.http.HTTPBinding
{
    public HTTPBinding(Transport t)
    {
        super(t);        
    }
}
