package org.codehaus.xfire.annotations;

/**
 * @author Arjen Poutsma
 */


import java.lang.reflect.Method;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.object.ObjectService;
import org.codehaus.xfire.service.object.Operation;
import org.codehaus.xfire.service.object.Parameter;
import org.codehaus.xfire.test.AbstractXFireTypeTest;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderInfo;
import org.easymock.MockControl;

public class AnnotationServiceFactoryTest
        extends AbstractXFireTypeTest
{
    private AnnotationServiceFactory annotationServiceFactory;
    private MockControl webAnnotationsControl;
    private WebAnnotations webAnnotations;

    public void setUp()
            throws Exception
    {
        super.setUp();

        webAnnotationsControl = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) webAnnotationsControl.getMock();
        annotationServiceFactory = new AnnotationServiceFactory(webAnnotations,
                getXFire().getTransportManager(),
                getRegistry());
    }

    public void testCreate()
            throws Exception
    {
        WebServiceAnnotation annotation = new WebServiceAnnotation();
        annotation.setServiceName("EchoService");
        annotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(annotation);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        annotationServiceFactory.create(EchoServiceImpl.class);

        webAnnotationsControl.verify();
    }

    public void testNoWebServiceAnnotation()
    {
        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(null);
        webAnnotationsControl.replay();

        try
        {
            annotationServiceFactory.create(EchoServiceImpl.class);
            fail("Not a XFireRuntimeException thrown");
        }
        catch (XFireRuntimeException e)
        {
            // expected behavior
        }
    }

    public void testEndpointInterface()
            throws SecurityException, NoSuchMethodException
    {
        WebServiceAnnotation implAnnotation = new WebServiceAnnotation();
        implAnnotation.setServiceName("EchoService");
        implAnnotation.setTargetNamespace("not used");
        implAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(implAnnotation);

        WebServiceAnnotation intfAnnotation = new WebServiceAnnotation();
        intfAnnotation.setName("Echo");
        intfAnnotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");
        intfAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoService.class);
        webAnnotationsControl.setReturnValue(intfAnnotation);

        Method echoMethod = EchoService.class.getMethod("echo", new Class[]{String.class});

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        Service service = annotationServiceFactory.create(EchoServiceImpl.class);
        assertEquals("http://xfire.codehaus.org/EchoService", service.getDefaultNamespace());
        assertEquals("EchoService", service.getName());

        WSDLBuilderInfo info = (WSDLBuilderInfo) service.getProperty(WSDLBuilderInfo.KEY);
        assertEquals("Echo", info.getPortType());
        assertEquals("EchoService", info.getServiceName());
        assertEquals("http://xfire.codehaus.org/EchoService", info.getTargetNamespace());

        webAnnotationsControl.verify();
    }

    public void testParameterNameAnnotation()
            throws SecurityException, NoSuchMethodException
    {
        WebServiceAnnotation implAnnotation = new WebServiceAnnotation();
        implAnnotation.setServiceName("EchoService");
        implAnnotation.setTargetNamespace("http://xfire.codehaus.org/EchoService");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(implAnnotation);

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        WebParamAnnotation paramAnnotation = new WebParamAnnotation();
        paramAnnotation.setName("input");
        webAnnotations.getWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(paramAnnotation);

        webAnnotations.hasWebParamAnnotation(echoMethod, 0);
        webAnnotationsControl.setReturnValue(true);

        webAnnotations.hasWebResultAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotations.hasOnewayAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);

        webAnnotationsControl.replay();

        ObjectService service = (ObjectService) annotationServiceFactory.create(EchoServiceImpl.class);
        assertEquals("http://xfire.codehaus.org/EchoService", service.getDefaultNamespace());
        assertEquals("EchoService", service.getName());

        final Operation operation = service.getOperation("echo");
        assertNotNull(operation);
        assertEquals(1, operation.getInParameters().size());
        assertEquals("input", ((Parameter) operation.getInParameters().iterator().next()).getName().getLocalPart());

        assertTrue(operation.isAsync());

        webAnnotationsControl.verify();
    }
}