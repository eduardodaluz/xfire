package org.codehaus.xfire;

import java.io.InputStream;
import junit.framework.TestCase;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.wsdl.WSDL;

/**
 * Contains helpful methods to test SOAP services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AbstractXFireTest
    extends TestCase
{
    private XFire xfire;
    
    public void setUp() throws Exception
    {
        xfire = new DefaultXFire();
    }
    
    /**
     * Invoke a service with the specified document.
     * 
     * @param service The name of the service.
     * @param document The request as an xml document in the classpath.
     * @return
     * @throws Exception
     */
    protected void invokeService( String service, String document ) 
        throws Exception
    {
         
       // return xfire.invoke( service, getResourceAsStream( document ), null );
    }

    /**
     * Get the WSDL for a service.
     * 
     * @param string The name of the service.
     * @return
     * @throws Exception
     */
    protected WSDL getWSDL(String service) 
        throws Exception
    {
        ServiceRegistry reg = getServiceRegistry();
        Service hello = reg.getService(service);
        
        return hello.getWSDL();
    }
    
    protected XFire getXFire()
    {
        return xfire;
    }
    
    protected ServiceRegistry getServiceRegistry()
    {
        return getXFire().getServiceRegistry();
    }
    
    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream(resource);
    }
}