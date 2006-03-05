package org.codehaus.xfire.security.wss4j;

import java.lang.reflect.Proxy;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;

public class RoundTripTest
    extends AbstractXFireAegisTest
{
    private WSS4JInHandler wsIn;
    private WSS4JOutHandler wsOut;
    private Service service;
    private Echo echo;
    private Client client;
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        service = getServiceFactory().create(Echo.class, "Echo", null, null);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        service.addInHandler(new DOMInHandler());
        service.addOutHandler(new DOMOutHandler());

        wsIn = new WSS4JInHandler();
        wsIn.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/insecurity.properties");
        wsIn.setProperty(WSHandlerConstants.DEC_PROP_FILE, "META-INF/xfire/insecurity.properties");
        wsIn.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, TestPwdCallback.class.getName());

        service.addInHandler(wsIn);
        
        wsOut = new WSS4JOutHandler();
        wsOut.setProperty(WSHandlerConstants.SIG_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        wsOut.setProperty(WSHandlerConstants.ENC_PROP_FILE, "META-INF/xfire/outsecurity.properties");
        wsOut.setProperty(WSHandlerConstants.USER, "alias");
        wsOut.setProperty("password", "aliaspass");
        wsOut.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
        wsOut.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, TestPwdCallback.class.getName());
        service.addOutHandler(wsOut);
        
        getServiceRegistry().register(service);
        
        // Create the client
        XFireProxyFactory pFactory = new XFireProxyFactory(getXFire());
        echo = (Echo) pFactory.create(service, "xfire.local://Echo");
        
        client = ((XFireProxy) Proxy.getInvocationHandler(echo)).getClient();
        client.addInHandler(wsIn);
        client.addInHandler(new DOMInHandler());
        client.addOutHandler(wsOut);
        client.addOutHandler(new DOMOutHandler());
    }

    public void testSignature()
        throws Exception
    {
        wsIn.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
        wsOut.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);

        assertEquals("test", echo.echo("test"));
    }

    public void testEncyprtionPlusSig()
        throws Exception
    {
        wsIn.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.SIGNATURE);
        wsOut.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.SIGNATURE);

        assertEquals("test", echo.echo("test"));
    }

    public void testUsernameToken()
        throws Exception
    {
        String actions = WSHandlerConstants.ENCRYPT + " " + 
            WSHandlerConstants.SIGNATURE + " " + 
            WSHandlerConstants.TIMESTAMP + " " + 
            WSHandlerConstants.USERNAME_TOKEN;
        
        wsIn.setProperty(WSHandlerConstants.ACTION, actions);
        wsOut.setProperty(WSHandlerConstants.ACTION, actions);

        assertEquals("test", echo.echo("test"));
    }
}
