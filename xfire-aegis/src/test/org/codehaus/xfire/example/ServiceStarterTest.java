package org.codehaus.xfire.example;

import junit.framework.TestCase;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.Echo;

public class ServiceStarterTest extends TestCase
{
  
    public void testService() throws Exception
    {
        // START SNIPPET: service
        ServiceStarter starter = new ServiceStarter();
        starter.start();
        
        // Create a service model for the client
        ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
        Service serviceModel = serviceFactory.create(Echo.class);

        // Create a client proxy
        XFireProxyFactory proxyFactory = new XFireProxyFactory();
        Echo echo = (Echo) proxyFactory.create(serviceModel, "http://localhost:8080/Echo");
        
        System.out.println(echo.echo("Hello World"));
        
        starter.stop();
        // END SNIPPET: service
    }
}
