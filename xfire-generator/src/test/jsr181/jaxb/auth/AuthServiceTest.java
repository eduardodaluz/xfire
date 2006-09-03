package jsr181.jaxb.auth;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.service.Service;

public class AuthServiceTest   
    extends AbstractXFireAegisTest
{
    private Service service;
    
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        JaxbServiceFactory asf = new JaxbServiceFactory(getXFire().getTransportManager());
        service = asf.create(AuthServiceCustomImpl.class);

        getServiceRegistry().register(service);
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        getServiceRegistry().unregister(service);
        super.tearDown();
    }
    
    @Override
    protected XFire getXFire()
    {
        return XFireFactory.newInstance().getXFire();
    }
    
    public void testEcho() throws Exception
    {   
        AuthServiceClient service = new AuthServiceClient();
        
        AuthServicePortType client = service.getAuthServicePortTypeLocalEndpoint();
        
        try
        {
            client.authenticate("foo", "bar");
            fail("Exception should've been thrown.");
        }
        catch (jsr181.jaxb.auth.AuthenticationFault e)
        {
        }
    }
}
