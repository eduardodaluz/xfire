package org.codehaus.xfire.annotations;


import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.easymock.MockControl;

public class AnnotationBasedServiceInfoAssemblerTest
        extends TestCase
{
    private AnnotationBasedServiceInfoAssembler assembler;
    private MockControl webAnnotationsControl;
    private WebAnnotations webAnnotations;

    protected void setUp()
            throws Exception
    {
        webAnnotationsControl = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) webAnnotationsControl.getMock();
        assembler = new AnnotationBasedServiceInfoAssembler(webAnnotations);
    }

    public void testGetOperationMethodsEndpointInterface()
            throws Exception
    {
        WebServiceAnnotation webServiceAnnotation = new WebServiceAnnotation();
        webServiceAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(webServiceAnnotation);

        webAnnotationsControl.replay();

        Method[] methods = assembler.getOperationMethods(EchoServiceImpl.class);
        assertNotNull(methods);
        assertEquals(1, methods.length);
        assertEquals("getOperationInfos does not honor endpoint interface",
                     EchoService.class,
                     methods[0].getDeclaringClass());

        webAnnotationsControl.verify();

    }

    public void testGetOperationMethodsNoEndpointInterface()
            throws Exception
    {
        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(new WebServiceAnnotation());

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        Method asyncMethod = EchoServiceImpl.class.getMethod("async", new Class[0]);

        webAnnotations.hasWebMethodAnnotation(echoMethod);
        webAnnotationsControl.setReturnValue(true);
        webAnnotations.hasWebMethodAnnotation(asyncMethod);
        webAnnotationsControl.setReturnValue(false);

        webAnnotationsControl.replay();

        Method[] methods = assembler.getOperationMethods(EchoServiceImpl.class);
        assertNotNull(methods);
        assertEquals(1, methods.length);
        assertEquals("getOperationInfos does not honor web method annotation",
                     EchoServiceImpl.class,
                     methods[0].getDeclaringClass());

        webAnnotationsControl.verify();
    }

    public void testPopulateServiceInfoDefaults()
            throws Exception
    {
        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation webServiceAnnotation = new WebServiceAnnotation();

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(webServiceAnnotation);

        webAnnotationsControl.replay();

        ServiceInfo serviceInfo = new ServiceInfo();

        assembler.populate(serviceInfo, EchoServiceImpl.class);
        assertEquals("EchoServiceImpl", serviceInfo.getName());
        assertEquals("http://annotations.xfire.codehaus.org", serviceInfo.getNamespace());

        webAnnotationsControl.verify();
    }

    public void testPopulateServiceInfoNonDefault()
            throws Exception
    {
        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(true);

        WebServiceAnnotation webServiceAnnotation = new WebServiceAnnotation();
        webServiceAnnotation.setName("Echo");
        webServiceAnnotation.setTargetNamespace("http://xfire.codehaus.org");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        webAnnotationsControl.setReturnValue(webServiceAnnotation);

        webAnnotationsControl.replay();

        ServiceInfo serviceInfo = new ServiceInfo();

        assembler.populate(serviceInfo, EchoServiceImpl.class);
        assertEquals("Echo", serviceInfo.getName());
        assertEquals("http://xfire.codehaus.org", serviceInfo.getNamespace());

        webAnnotationsControl.verify();
    }

    public void methodNoArgs()
    {
        // this method is here for the testPopulateMethodInfoOneWay() tests
    }

    public void methodArgs(String arg)
    {
        // this method is here for the testPopulateMethodInfoUnsuitableOneway() tests
    }


    public void testPopulateMethodInfoOneWay()
            throws Exception
    {
        Method method = getClass().getMethod("methodNoArgs", new Class[0]);
        webAnnotations.hasWebMethodAnnotation(method);
        webAnnotationsControl.setReturnValue(true);

        WebMethodAnnotation webMethodAnnotation = new WebMethodAnnotation();
        webMethodAnnotation.setOperationName("operation");
        webAnnotations.getWebMethodAnnotation(method);
        webAnnotationsControl.setReturnValue(webMethodAnnotation);

        webAnnotations.hasOnewayAnnotation(method);
        webAnnotationsControl.setReturnValue(true);

        webAnnotationsControl.replay();

        OperationInfo operationInfo = new OperationInfo("method");
        assembler.populate(operationInfo, method);
        assertEquals(webMethodAnnotation.getOperationName(), operationInfo.getName());
        assertTrue(operationInfo.isOneWay());
        webAnnotationsControl.verify();
    }

    public void testPopulateMethodInfoUnsuitableOneway()
            throws Exception
    {
        Method method = getClass().getMethod("methodArgs", new Class[]{String.class});
        webAnnotations.hasWebMethodAnnotation(method);
        webAnnotationsControl.setReturnValue(true);

        WebMethodAnnotation webMethodAnnotation = new WebMethodAnnotation();
        webAnnotations.getWebMethodAnnotation(method);
        webAnnotationsControl.setReturnValue(webMethodAnnotation);

        webAnnotations.hasOnewayAnnotation(method);
        webAnnotationsControl.setReturnValue(true);

        webAnnotationsControl.replay();

        OperationInfo operationInfo = new OperationInfo("method");

        try
        {
            assembler.populate(operationInfo, method);
            fail("AnnotationException not thrown");
        }
        catch (AnnotationException e)
        {
            //Expected behavior
        }
    }
}