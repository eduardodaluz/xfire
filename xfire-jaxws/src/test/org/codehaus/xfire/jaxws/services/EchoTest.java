package org.codehaus.xfire.jaxws.services;

import javax.xml.ws.Endpoint;

import org.codehaus.xfire.jaxws.AbstractJAXWSTest;
import org.codehaus.xfire.jaxws.services.EchoImpl;
import org.codehaus.xfire.server.http.XFireHttpServer;

import services.echo.EchoPortType;
import services.echo.EchoService;

public class EchoTest
    extends AbstractJAXWSTest
{
    private XFireHttpServer server;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
        
        Endpoint.publish("http://localhost:8191/Echo", new EchoImpl());
    }
    
    
    @Override
    protected void tearDown()
        throws Exception
    {
        server.stop();
        
        super.tearDown();
    }


    public void testService() throws Exception
    {
        EchoService service = new EchoService();
        
        EchoPortType echo = service.getEchoHttpPort();
        assertEquals("echo", echo.echo("echo"));
    }
}
