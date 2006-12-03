package org.codehaus.xfire.gen;

import java.util.Collection;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.spring.XFireConfigLoader;

public class BaseServicesGenTask
    extends Task
{
    private String configUrl;
    
    private ClassLoader overridingContextClassLoader = XFireConfigLoader.class.getClassLoader();
    
    
    public void iterateServices(){
        //Ugly fix for XFIRE-245 & similar: wsgen can't find XMLInputFactory
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        // displayClasspath(originalCL, "originalCL");

        Thread.currentThread().setContextClassLoader(overridingContextClassLoader);
        // displayClasspath(overridingContextClassLoader, "classLoader");

        XFireConfigLoader configLoader = new XFireConfigLoader();

        XFire xfire;

        try {
            xfire = configLoader.loadConfig( configUrl );
        } catch (XFireException e) {
            throw new BuildException( "Failure to load the configUrl", e);
        }

        final ServiceRegistry serviceRegistry = xfire.getServiceRegistry();

        Collection services = serviceRegistry.getServices();

        
        

        for (Iterator iterator = services.iterator(); iterator.hasNext();)
        {

            Service service = (Service) iterator.next();
            processService(service);

        }

        Thread.currentThread().setContextClassLoader(originalCL);

    }
    
     protected void processService(Service service){
         
     }

    public String getConfigUrl()
    {
        return configUrl;
    }

    public void setConfigUrl(String configUrl)
    {
        this.configUrl = configUrl;
    }

     
     
}
