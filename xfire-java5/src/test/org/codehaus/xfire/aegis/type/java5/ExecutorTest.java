package org.codehaus.xfire.aegis.type.java5;

import java.util.concurrent.Executor;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Echo;
import org.codehaus.xfire.service.EchoImpl;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.ObjectInvoker;

public class ExecutorTest extends AbstractXFireAegisTest
{
    public void testJava5Executor() throws Exception
    {
        Service service = getServiceFactory().create(Echo.class);
        service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, EchoImpl.class);
        
        getServiceRegistry().register(service);
        
        service.setExecutor(new TestExecutor());
        
        invokeService("Echo", "echo11.xml");
        
        assertTrue(TestExecutor.run);
    }
    
    public static class TestExecutor implements Executor
    {
        static boolean run;
        
        public void execute(Runnable r)
        {
            run = true;
            r.run();
        }
    }
}
