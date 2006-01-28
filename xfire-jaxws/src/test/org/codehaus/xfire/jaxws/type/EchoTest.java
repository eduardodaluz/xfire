package org.codehaus.xfire.jaxws.type;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;

import org.codehaus.xfire.jaxws.AbstractJAXWSHttpTest;

import services.headerout.EchoPortType;
import services.headerout.EchoService;
import echo.wrapped.Echo;
import echo.wrapped.EchoResponse;

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
        
        EchoPortType client = service.getEchoHttpPort();
        
        Holder<EchoResponse> out2 = new Holder<EchoResponse>();
        Holder<EchoResponse> outHeader = new Holder<EchoResponse>();
        
        Echo echo = new Echo();
        echo.setText("hi");
        
        Echo echoHeader = new Echo();
        echoHeader.setText("header");
        
        assertEquals("hi", client.echo(echo, echoHeader, out2, outHeader).getText());
        assertEquals("header", outHeader.value.getText());
        assertEquals("hi", out2.value.getText());
    }
}
