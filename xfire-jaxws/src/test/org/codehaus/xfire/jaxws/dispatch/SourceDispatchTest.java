package org.codehaus.xfire.jaxws.dispatch;

import javax.xml.ws.Endpoint;

import org.codehaus.xfire.jaxws.AbstractJAXWSTest;
import org.codehaus.xfire.jaxws.services.EchoImpl;

import services.echo.EchoPortType;
import services.echo.EchoService;

public class SourceDispatchTest
    extends AbstractJAXWSTest
{
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        Endpoint.publish("xfire.local://Echo", new EchoImpl());
    }
    
    public void testPayloadSourceDispatch() throws Exception
    {
        EchoService service = new EchoService();
        
        EchoPortType echo = service.getEchoLocalPort();
        assertEquals("echo", echo.echo("echo"));
    }
}
