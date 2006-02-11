// START SNIPPET: service
package org.codehaus.xfire.example;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.server.http.XFireHttpServer;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectInvoker;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.test.Echo;
import org.codehaus.xfire.test.EchoImpl;

/**
 * Creates an Echo service and exposes it via HTTP.
 */
public class ServiceStarter
{
    XFireHttpServer server;
    
    public void start() throws Exception
    {
        // Create an XFire Service
        ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
        Service service = serviceFactory.create(Echo.class);
        // Set the implementation class
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        // Register the service in the ServiceRegistry
        XFire xfire = XFireFactory.newInstance().getXFire();
        xfire.getServiceRegistry().register(service);
        
        // Start the HTTP server
        server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
    }
    
    public void stop() throws Exception
    {
        server.stop();
    }
}
// END SNIPPET: service