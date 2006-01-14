package org.codehaus.xfire.spring;


/**
 * @author Arjen Poutsma
 */

import java.util.List;

import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.HandlerPipeline;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceComponentTest
        extends AbstractXFireSpringTest
{
    public void testSpringIntegration()
            throws Exception
    {
        ApplicationContext appContext = getContext();
        
        assertNotNull(appContext.getBean("xfire.serviceFactory"));
        assertNotNull(appContext.getBean("echo"));
        
        ServiceBean service = (ServiceBean) appContext.getBean("echoService");
        assertNotNull(service);
        
        assertNotNull(service.getXFireService());
        
        ServiceRegistry reg = (ServiceRegistry) appContext.getBean("xfire.serviceRegistry");
        assertTrue(reg.hasService(service.getXFireService().getSimpleName()));
        
        assertNotNull(service.getInHandlers());
    }
    
    public void testPhasePropertyOfHandlers() throws Exception 
    {
        AbstractHandler unchangedHandler = (AbstractHandler) getContext().getBean("addressingHandler");
        assertEquals("pre-dispatch",unchangedHandler.getPhase());
        
        AbstractHandler handler = (AbstractHandler) getContext().getBean("changedPhaseHandler");
        assertEquals("pre-invoke",handler.getPhase());
        
    }

    public void testNoIntf()
            throws Exception
    {
        ServiceBean service = (ServiceBean) getContext().getBean("echoService");
        assertNotNull(service);
        assertEquals("Echo", service.getXFireService().getSimpleName());
    }
    
    public void testHandlerOrderingBefore() throws Exception
    {
        ServiceBean service = (ServiceBean) getContext().getBean("firstBeforeSecond");
        AbstractHandler testHandler = (AbstractHandler) getContext().getBean("firstHandler");
        AbstractHandler testHandler2 = (AbstractHandler) getContext().getBean("secondHandler");

        HandlerPipeline pipeline = new HandlerPipeline(getXFire().getInPhases());
        pipeline.addHandlers(service.getInHandlers());

        List inHandlers = pipeline.getPhaseHandlers(Phase.USER).getHandlers();
        int firstPos = inHandlers.indexOf(testHandler);
        int secondPos = inHandlers.indexOf(testHandler2);
        assertTrue(firstPos != -1);
        assertTrue(secondPos != -1);
        assertTrue(firstPos < secondPos);
    }

    public void testHandlerOrderingAfter() throws Exception
    {
        ServiceBean service = (ServiceBean) getContext().getBean("firstAfterSecond");
        AbstractHandler testHandler = (AbstractHandler) getContext().getBean("firstHandler2");
        AbstractHandler testHandler2 = (AbstractHandler) getContext().getBean("secondHandler2");

        HandlerPipeline pipeline = new HandlerPipeline(getXFire().getInPhases());
        pipeline.addHandlers(service.getInHandlers());

        List inHandlers = pipeline.getPhaseHandlers(Phase.USER).getHandlers();
        int firstPos = inHandlers.indexOf(testHandler);
        int secondPos = inHandlers.indexOf(testHandler2);
        assertTrue(firstPos != -1);
        assertTrue(secondPos != -1);
        assertTrue(firstPos > secondPos);
    }
    
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[]{
                "/org/codehaus/xfire/spring/xfire.xml",
                "/org/codehaus/xfire/spring/serviceBean.xml"});
    }
    
}