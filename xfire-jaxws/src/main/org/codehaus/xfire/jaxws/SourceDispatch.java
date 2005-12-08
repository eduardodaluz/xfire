package org.codehaus.xfire.jaxws;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Binding;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;

public class SourceDispatch
    implements javax.xml.ws.Dispatch<Source>
{
    private Client client;
    private Service.Mode mode;
    private OperationInfo opInfo;
    
    public SourceDispatch(Client client)
    {
        this.client = client;
    
        opInfo = client.getService().getServiceInfo().getOperation("invoke");
    }

    public Source invoke(Source source)
    {
        try
        {
            Object[] result = client.invoke(opInfo, new Object[] { source });
            if (result.length == 0) return null;
            
            return (Source) result[0];
        }
        catch (Exception e)
        {
            throw new WebServiceException(e);
        }
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

    public Service.Mode getMode()
    {
        return mode;
    }

    public void setMode(Service.Mode mode)
    {
        this.mode = mode;
    }

    
}
