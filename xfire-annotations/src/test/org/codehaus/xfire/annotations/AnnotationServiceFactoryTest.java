package org.codehaus.xfire.annotations;

/**
 * @author Arjen Poutsma
 */


import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.service.DefaultServiceRegistry;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.DefaultTransportManager;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.type.TypeMappingRegistry;
import org.easymock.MockControl;

public class AnnotationServiceFactoryTest
        extends TestCase
{
    private AnnotationServiceFactory annotationServiceFactory;
    private MockControl webAnnotationsControl;
    private WebAnnotations webAnnotations;

    protected void setUp()
            throws Exception
    {
        webAnnotationsControl = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) webAnnotationsControl.getMock();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        TransportManager transportManager = new DefaultTransportManager(serviceRegistry);
        TypeMappingRegistry typeMappingRegistry = new DefaultTypeMappingRegistry(true);
        annotationServiceFactory = new AnnotationServiceFactory(webAnnotations, transportManager, typeMappingRegistry);
    }

    public void testCreate()
            throws Exception
    {
        WebServiceAnnotation annotation = new WebServiceAnnotation();
        annotation.setName("EchoService");
        annotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(annotation);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotationsControl.replay();

        annotationServiceFactory.create(EchoServiceImpl.class);

        webAnnotationsControl.verify();
    }
}