package org.codehaus.xfire.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.jdom.Element;
import org.xml.sax.InputSource;

public class XFireProxyTest
        extends AbstractXFireTest
{
    private XFireProxyFactory factory;
    private Service service;
    private Transport transport;
    
    public void setUp() throws Exception
    {
        super.setUp();

        service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);

        getServiceRegistry().register(service);
        factory = new XFireProxyFactory();
        
        transport = getTransportManager().getTransport(LocalTransport.BINDING_ID);
    }
    
    public void testHandleEquals()
            throws Exception
    {
        Echo echoProxy1 = (Echo) factory.create(service, transport, "");

        assertEquals(echoProxy1, echoProxy1);
    }

    public void testHandleHashCode()
            throws Exception
    {
        Echo echoProxy = (Echo) factory.create(service, transport, "");
        
        assertTrue(echoProxy.hashCode() != 0);
    }
    
    public void testInvoke() throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) factory.create(service, transport, "xfire.local://Echo");
        
        Element e = echo.echo(root);
        assertEquals(root.getName(), e.getName());
    }
    
    public void testInvokeDifferentBinding() throws Exception
    {
        Element root = new Element("root", "a", "urn:a");
        root.addContent("hello");
        
        Service serviceModel = new ObjectServiceFactory(new MessageBindingProvider()).create(Echo.class);
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Echo echo = (Echo) factory.create(serviceModel, "xfire.local://Echo");
        
        Element e = echo.echo(root);
        assertEquals(root.getName(), e.getName());
    }
    
    public void testInvokeWithWsdl() throws Exception
    {
        Service service = getServiceFactory().create(StringEcho.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, StringEchoImpl.class);
        getServiceRegistry().register(service);
        
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	getXFire().generateWSDL("StringEcho", baos);
    	String wsdl = baos.toString();
    	System.err.println(wsdl);
        Definition desc = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, new InputSource(new ByteArrayInputStream(wsdl.getBytes())));
        
        XFireProxyFactory factory = new XFireProxyFactory(getXFire());
        Client client = new Client(desc, Echo.class);
        StringEcho echo = (StringEcho) factory.create(client);
        
        String msg = "Hello world !";
        String ans = echo.echo(msg);
        assertEquals(msg, ans);
    }
    
    public static interface StringEcho
    {
    	String echo(String msg);
    }
    
    public static class StringEchoImpl implements StringEcho
    {
    	public String echo(String msg)
    	{
    		return msg;
    	}
    }
}