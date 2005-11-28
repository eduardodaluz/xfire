package org.codehaus.xfire.jaxws.handler;

import java.util.HashMap;
import java.util.Map;

public class AbstractMessageContext
    extends HashMap<String,Object>
    implements javax.xml.ws.handler.MessageContext
{
    private Map<String,Scope> prop2scope = new HashMap<String,Scope>();
    
    public Scope getScope(String property)
    {
        return prop2scope.get(property);
    }

    public void setScope(String property, Scope scope)
    {
        prop2scope.put(property, scope);
    }
}
