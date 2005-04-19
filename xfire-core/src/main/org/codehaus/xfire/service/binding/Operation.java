package org.codehaus.xfire.service.binding;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

/**
 * An operation that be performed on a service.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 20, 2004
 */
public class Operation
{
    private LinkedHashMap inParams;

    private LinkedHashMap outParams;
        
    private Method method;

    private boolean async = false;
    
    public Operation( final Method method )
    {
        inParams = new LinkedHashMap();
        outParams = new LinkedHashMap();

        this.method = method;
    }

    public boolean isAsync()
    {
        return async;
    }

    public void setAsync(boolean async)
    {
        this.async = async;
    }

    public void addInParameter( final Parameter p )
    {
        inParams.put( p.getName(), p );
    }
    
    public Parameter removeInParameter( final QName q )
    {
        return (Parameter) inParams.remove( q );
    }
    
    public Parameter getInParameter( final QName q )
    {
        return (Parameter) inParams.get(q);
    }
    
    public Collection getInParameters()
    {
        return inParams.values();
    }

    public void addOutParameter( final Parameter p )
    {
        outParams.put( p.getName(), p );
    }

    public Parameter removeOutParameter( final QName q )
    {
        return (Parameter) outParams.remove( q );
    }
    
    public Parameter getOutParameter( final QName q )
    {
        return (Parameter) outParams.get(q);
    }
    
    public Collection getOutParameters()
    {
        return outParams.values();
    }

    public Method getMethod()
    {
        return method;
    }

    public String getName()
    {
        return method.getName();
    }
}
