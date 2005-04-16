package org.codehaus.xfire.spring;

/**
 * @author Arjen Poutsma
 */

import java.lang.reflect.Method;

import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.WebServiceAnnotation;
import org.codehaus.xfire.test.AbstractXFireTypeTest;
import org.easymock.MockControl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class XFireWebAnnotationsHandlerMappingTest
        extends AbstractXFireTypeTest
{
    private XFireWebAnnotationsHandlerMapping handlerMapping;
    private MockControl control;
    private WebAnnotations webAnnotations;


    public void setUp()
            throws Exception
    {
        super.setUp();
        handlerMapping = new XFireWebAnnotationsHandlerMapping();
        control = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) control.getMock();
        handlerMapping.setWebAnnotations(webAnnotations);
        handlerMapping.setXfire(getXFire());
        handlerMapping.setTypeMappingRegistry(getRegistry());
    }

    public void testHandler()
            throws Exception
    {
        StaticWebApplicationContext appContext = new StaticWebApplicationContext();
        appContext.registerSingleton("echo", EchoImpl.class, new MutablePropertyValues());

        webAnnotations.hasWebServiceAnnotation(EchoImpl.class);
        control.setReturnValue(true);
        webAnnotations.hasSOAPBindingAnnotation(EchoImpl.class);
        control.setReturnValue(false);
        webAnnotations.hasWebServiceAnnotation(EchoImpl.class);
        control.setReturnValue(true);
        WebServiceAnnotation serviceAnnotation = new WebServiceAnnotation();
        serviceAnnotation.setServiceName("EchoService");
        webAnnotations.getWebServiceAnnotation(EchoImpl.class);
        control.setReturnValue(serviceAnnotation);
        Method echoMethod = EchoImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        control.setReturnValue(true);
        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        control.setReturnValue(false);
        webAnnotations.hasWebResultAnnotation(echoMethod);
        control.setReturnValue(false);
        webAnnotations.hasOnewayAnnotation(echoMethod);
        control.setReturnValue(false);

        control.replay();

        String urlPrefix = "/services/";
        handlerMapping.setUrlPrefix(urlPrefix);
        handlerMapping.setApplicationContext(appContext);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", urlPrefix + "EchoService");
        Object handler = handlerMapping.getHandler(request);
        assertNotNull("No valid handler is returned", handler);

        control.verify();
    }

    public void testNoAnnotation()
            throws Exception
    {
        StaticWebApplicationContext appContext = new StaticWebApplicationContext();
        appContext.registerSingleton("echo", EchoImpl.class, new MutablePropertyValues());

        webAnnotations.hasWebServiceAnnotation(EchoImpl.class);
        control.setReturnValue(false);

        control.replay();

        handlerMapping.setApplicationContext(appContext);


        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/services/EchoService");
        Object handler = handlerMapping.getHandler(request);
        assertNull("Handler is returned", handler);

        control.verify();
    }
}