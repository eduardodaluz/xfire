package org.codehaus.xfire.jaxws.services;

import javax.xml.ws.Endpoint;

import org.codehaus.xfire.jaxws.AbstractJAXWSHttpTest;

import services.echo.EchoPortType;
import services.echo.EchoService;

public class EchoTest
    extends AbstractJAXWSHttpTest
{
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        Endpoint.publish("http://localhost:8191/Echo", new EchoImpl());
    }

    public void testService() throws Exception
    {
        EchoService service = new EchoService();
        
        EchoPortType echo = service.getEchoHttpPort();
        assertEquals("echo", echo.echo("echo"));
    }
}
