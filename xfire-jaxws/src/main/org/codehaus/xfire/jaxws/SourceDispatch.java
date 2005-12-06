package org.codehaus.xfire.jaxws;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Binding;
import javax.xml.ws.Response;

import org.codehaus.xfire.client.Client;

public class SourceDispatch
    implements javax.xml.ws.Dispatch<Source>
{
    Client client;
    
    public Source invoke(Source source)
    {
        
        return null;
    }

    public Future< ? > invokeAsync(Source source, AsyncHandler<Source> arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Response<Source> invokeAsync(Source source)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void invokeOneWay(Source source)
    {
        // TODO Auto-generated method stub
        
    }

    public Binding getBinding()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, Object> getRequestContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, Object> getResponseContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
