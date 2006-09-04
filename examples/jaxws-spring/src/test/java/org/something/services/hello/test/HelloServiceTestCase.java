package org.something.services.hello.test;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.remoting.XFireExporter;
import org.something.services.hello.HelloRequest;
import org.something.services.hello.HelloResponse;
import org.something.services.hello.ws.HelloFaultMessage;
import org.something.services.hello.ws.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello Service TestCase. 
 * @author Michael Vorburger
 */
public class HelloServiceTestCase extends TestCase {

	protected ApplicationContext serverCtx = 
		new ClassPathXmlApplicationContext("/server.beans.xml");

	protected ApplicationContext remoteClientCtx = 
		new ClassPathXmlApplicationContext("/client.beans.xml");
	
	protected static final int LOCALHOST_PORT = 8191;
	protected static final String LOCALHOST_URL = "http://localhost:8191/HelloService";

	/**
	 * Test the two operations via a 'remote' service call.
	 * Remote as in localhost HTTP call; the test starts a local (embedded Jetty) for this.
	 * 
	 * @throws Exception
	 */
	public void testViaXFireServer() throws Exception {
		HelloService remoteHelloService = getClient();
		XFireExporter exporter = (XFireExporter) serverCtx.getBean("hello.server");

		// Use XFire-specific XFireHttpServer; JAX-WS javax.xml.ws.Endpoint doesn't work... ;-(
		XFireHttpServer httpServer = this.startXFireServer(LOCALHOST_PORT, exporter);
		// Endpoint httpServer = Endpoint.publish(LOCALHOST_URL, exporter.getServiceBean());
		
		checkHelloServiceSayHello(remoteHelloService);
		checkHelloServiceFault(remoteHelloService);
		httpServer.stop();
	}
	
	private void checkHelloServiceSayHello(HelloService helloService) throws HelloFaultMessage {
		HelloRequest request = new HelloRequest();
		request.setToWho("World!");
		HelloResponse response = helloService.sayHello(request);
		assertEquals("Hello World!", response.getGreeting());
	}

	private void checkHelloServiceFault(HelloService helloService) {
		HelloRequest request = new HelloRequest();
		request.setToWho("FAULT");
		try {
			helloService.sayHello(request);
			fail("Should have thrown an Exception");
		}
		catch (HelloFaultMessage exception) {
		}
	}

	private XFireHttpServer startXFireServer(int httpPort, XFireExporter exporter) throws Exception {
		Service service = exporter.getXFireService(); 

        XFire xfire = XFireFactory.newInstance().getXFire();
        xfire.getServiceRegistry().register(service);
        
		XFireHttpServer server = new XFireHttpServer();
		server.setPort(httpPort);
		server.start();
		
		return server;
	}	
	
	private HelloService getClient() { 
		HelloService remoteHelloService = (HelloService) remoteClientCtx.getBean("hello.client");
		// Change the URL ('Service Endpoint') of the remote service 
		Client client = ((XFireProxy) Proxy.getInvocationHandler(remoteHelloService)).getClient();
		client.setUrl(LOCALHOST_URL);
		return remoteHelloService;
	}
}
