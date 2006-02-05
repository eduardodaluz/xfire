package org.codehaus.xfire.jaxws.services;

import javax.xml.ws.Endpoint;

import org.codehaus.xfire.jaxws.AbstractJAXWSHttpTest;

import services.auth.AuthServicePortType;
import services.auth.AuthServiceService;
import services.auth.AuthenticationFault_Exception;

public class AuthTest
    extends AbstractJAXWSHttpTest
{
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        Endpoint.publish("http://localhost:8191/AuthService", new AuthServiceImpl());
    }

    public void testService() throws Exception
    {
        AuthServiceService service = new AuthServiceService();
        
        AuthServicePortType auth = service.getAuthServiceHttpPort();

        try
        {
            auth.authenticate("bleh", "bar");
            fail("Must throw fault.");
        }
        catch (AuthenticationFault_Exception f)
        {
        }
    }
}
