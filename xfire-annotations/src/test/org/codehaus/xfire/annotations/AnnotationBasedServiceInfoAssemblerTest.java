package org.codehaus.xfire.annotations;


import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;
import org.easymock.MockControl;

public class AnnotationBasedServiceInfoAssemblerTest
        extends TestCase
{
    private AnnotationBasedServiceInfoAssembler assembler;
    private MockControl mockControl;
    private WebAnnotations webAnnotations;
    private Method methodArgs;
    private Method methodNoArgs;
    private OperationInfo operation;

    protected void setUp()
            throws Exception
    {
        mockControl = MockControl.createControl(WebAnnotations.class);
        webAnnotations = (WebAnnotations) mockControl.getMock();

        assembler = new AnnotationBasedServiceInfoAssembler(EchoServiceImpl.class, webAnnotations);

        methodArgs = getClass().getMethod("methodArgs", new Class[]{String.class});
        methodNoArgs = getClass().getMethod("methodNoArgs", new Class[0]);

        ServiceInfo serviceInfo = new ServiceInfo();
        operation = serviceInfo.addOperation("method");
    }

    public void methodNoArgs()
    {
        // this method is here for the tests
    }

    public String methodArgs(String arg)
    {
        // this method is here for the tests
        return arg;
    }

    public void testGetOperationMethodsEndpointInterface()
            throws Exception
    {
        WebServiceAnnotation webServiceAnnotation = new WebServiceAnnotation();
        webServiceAnnotation.setEndpointInterface(EchoService.class.getName());

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        mockControl.setReturnValue(webServiceAnnotation);

        mockControl.replay();

        Method[] methods = assembler.getOperationMethods(EchoServiceImpl.class);
        assertNotNull(methods);
        assertEquals(1, methods.length);
        assertEquals("getOperations does not honor endpoint interface",
                     EchoService.class,
                     methods[0].getDeclaringClass());

        mockControl.verify();

    }

    public void testGetOperationMethodsNoEndpointInterface()
            throws Exception
    {
        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        mockControl.setReturnValue(new WebServiceAnnotation());

        Method echoMethod = EchoServiceImpl.class.getMethod("echo", new Class[]{String.class});
        Method asyncMethod = EchoServiceImpl.class.getMethod("async", new Class[0]);

        webAnnotations.hasWebMethodAnnotation(echoMethod);
        mockControl.setReturnValue(true);
        webAnnotations.hasWebMethodAnnotation(asyncMethod);
        mockControl.setReturnValue(false);

        mockControl.replay();

        Method[] methods = assembler.getOperationMethods(EchoServiceImpl.class);
        assertNotNull(methods);
        assertEquals(1, methods.length);
        assertEquals("getOperations does not honor web method annotation",
                     EchoServiceImpl.class,
                     methods[0].getDeclaringClass());

        mockControl.verify();
    }

    public void testPopulateServiceInfo()
            throws Exception
    {
        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        mockControl.setReturnValue(true);

        WebServiceAnnotation webServiceAnnotation = new WebServiceAnnotation();
        webServiceAnnotation.setName("Echo");
        webServiceAnnotation.setTargetNamespace("http://xfire.codehaus.org");

        webAnnotations.getWebServiceAnnotation(EchoServiceImpl.class);
        mockControl.setReturnValue(webServiceAnnotation);

        mockControl.replay();

        ServiceInfo serviceInfo = new ServiceInfo();

        assembler.populateServiceInfo(serviceInfo, EchoServiceImpl.class);
        assertEquals("Echo", serviceInfo.getName());
        assertEquals("http://xfire.codehaus.org", serviceInfo.getNamespace());

        mockControl.verify();
    }

    public void testPopulateServiceInfoNoAnnotation()
            throws Exception
    {
        webAnnotations.hasWebServiceAnnotation(EchoServiceImpl.class);
        mockControl.setReturnValue(false);

        mockControl.replay();
        ServiceInfo serviceInfo = new ServiceInfo();
        try
        {
            assembler.populateServiceInfo(serviceInfo, EchoServiceImpl.class);
            fail("AnnotationsException not thrown");
        }
        catch (AnnotationException e)
        {
            // Expected behavior
        }
    }


    public void testPopulateOperationInfo()
            throws Exception
    {
        webAnnotations.hasWebMethodAnnotation(methodNoArgs);
        mockControl.setReturnValue(true);

        WebMethodAnnotation webMethodAnnotation = new WebMethodAnnotation();
        webMethodAnnotation.setOperationName("operation");
        webAnnotations.getWebMethodAnnotation(methodNoArgs);
        mockControl.setReturnValue(webMethodAnnotation);

        webAnnotations.hasOnewayAnnotation(methodNoArgs);
        mockControl.setReturnValue(false);

        mockControl.replay();

        assembler.populateOperationInfo(operation, methodNoArgs);
        assertEquals(webMethodAnnotation.getOperationName(), operation.getName());
        assertFalse(operation.isOneWay());
        mockControl.verify();
    }

    public void testPopulateOperationInfoNoAnnotation()
            throws Exception
    {

        webAnnotations.hasWebMethodAnnotation(methodArgs);
        mockControl.setReturnValue(false);

        mockControl.replay();

        assembler.populateOperationInfo(operation, methodArgs);
        assertEquals("method", operation.getName());
        assertFalse(operation.isOneWay());
        mockControl.verify();
    }

    public void testPopulateOperationInfoOneWay()
            throws Exception
    {
        webAnnotations.hasWebMethodAnnotation(methodNoArgs);
        mockControl.setReturnValue(true);

        WebMethodAnnotation webMethodAnnotation = new WebMethodAnnotation();
        webMethodAnnotation.setOperationName("operation");
        webAnnotations.getWebMethodAnnotation(methodNoArgs);
        mockControl.setReturnValue(webMethodAnnotation);

        webAnnotations.hasOnewayAnnotation(methodNoArgs);
        mockControl.setReturnValue(true);

        mockControl.replay();

        assembler.populateOperationInfo(operation, methodNoArgs);
        assertEquals(webMethodAnnotation.getOperationName(), operation.getName());
        assertTrue(operation.isOneWay());
        mockControl.verify();
    }

    public void testPopulateOperationInfoUnsuitableOneway()
            throws Exception
    {
        webAnnotations.hasWebMethodAnnotation(methodArgs);
        mockControl.setReturnValue(true);

        WebMethodAnnotation webMethodAnnotation = new WebMethodAnnotation();
        webAnnotations.getWebMethodAnnotation(methodArgs);
        mockControl.setReturnValue(webMethodAnnotation);

        webAnnotations.hasOnewayAnnotation(methodArgs);
        mockControl.setReturnValue(true);

        mockControl.replay();

        try
        {
            assembler.populateOperationInfo(operation, methodArgs);
            fail("AnnotationException not thrown");
        }
        catch (AnnotationException e)
        {
            // Expected behavior
        }
        mockControl.verify();
    }

    public void testGetInputMessageInfoNoWebParam()
            throws Exception
    {
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(false);
        mockControl.replay();
        MessageInfo messageInfo = assembler.getInputMessage(methodArgs, operation);
        assertNotNull(messageInfo);
        assertEquals("methodArgsRequest", messageInfo.getName());
        assertEquals(1, messageInfo.getMessageParts().size());
        MessagePartInfo part = messageInfo.getMessagePart("methodArgsRequestin0");
        assertNotNull(part);
        mockControl.verify();
    }

    public void testGetInputMessageInfoWebParam()
            throws Exception
    {
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(true);
        WebParamAnnotation annotation = new WebParamAnnotation();
        annotation.setName("name");
        webAnnotations.getWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(annotation);
        mockControl.replay();

        MessageInfo messageInfo = assembler.getInputMessage(methodArgs, operation);
        assertNotNull(messageInfo);
        assertEquals("methodArgsRequest", messageInfo.getName());
        assertEquals(1, messageInfo.getMessageParts().size());
        assertNotNull(messageInfo.getMessagePart("name"));
        mockControl.verify();
    }

    public void testGetInputMessageNoParts()
            throws Exception
    {
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(true);
        WebParamAnnotation annotation = new WebParamAnnotation();
        annotation.setHeader(true);
        webAnnotations.getWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(annotation);
        mockControl.replay();

        assertNull(assembler.getInputMessage(methodArgs, operation));
        mockControl.verify();
    }


    public void testGetOutputMessageInfoNoAnnotations()
            throws Exception
    {
        webAnnotations.hasWebResultAnnotation(methodArgs);
        mockControl.setReturnValue(false);
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(false);
        mockControl.replay();


        MessageInfo messageInfo = assembler.getOutputMessage(methodArgs, operation);
        assertNotNull(messageInfo);
        assertEquals("methodArgsResponse", messageInfo.getName());
        assertEquals(1, messageInfo.getMessageParts().size());
        assertNotNull(messageInfo.getMessagePart("methodArgsResponseout0"));
        mockControl.verify();
    }

    public void testGetOutputMessageInfoWebResultWebParam()
            throws Exception
    {
        webAnnotations.hasWebResultAnnotation(methodArgs);
        mockControl.setReturnValue(true);
        WebResultAnnotation resultAnnotation = new WebResultAnnotation();
        resultAnnotation.setName("name1");
        webAnnotations.getWebResultAnnotation(methodArgs);
        mockControl.setReturnValue(resultAnnotation);
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(true);
        WebParamAnnotation paramAnnotation = new WebParamAnnotation();
        paramAnnotation.setMode(WebParamAnnotation.MODE_OUT);
        paramAnnotation.setName("name2");
        webAnnotations.getWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(paramAnnotation);
        mockControl.replay();

        MessageInfo messageInfo = assembler.getOutputMessage(methodArgs, operation);
        assertNotNull(messageInfo);
        assertEquals("methodArgsResponse", messageInfo.getName());
        assertEquals(2, messageInfo.getMessageParts().size());
        assertNotNull(messageInfo.getMessagePart("name1"));
        assertNotNull(messageInfo.getMessagePart("name2"));
        mockControl.verify();
    }

    public void testGetOutputMessageInfoWebResult()
            throws Exception
    {
        webAnnotations.hasWebResultAnnotation(methodArgs);
        mockControl.setReturnValue(true);
        WebResultAnnotation webResultAnnotation = new WebResultAnnotation();
        webResultAnnotation.setName("name");
        webAnnotations.getWebResultAnnotation(methodArgs);
        mockControl.setReturnValue(webResultAnnotation);
        webAnnotations.hasWebParamAnnotation(methodArgs, 0);
        mockControl.setReturnValue(false);
        mockControl.replay();

        MessageInfo messageInfo = assembler.getOutputMessage(methodArgs, operation);
        assertNotNull(messageInfo);
        assertEquals("methodArgsResponse", messageInfo.getName());
        assertEquals(1, messageInfo.getMessageParts().size());
        assertNotNull(messageInfo.getMessagePart("name"));
        mockControl.verify();
    }

    public void testGetOutputMessageNoParts()
            throws Exception
    {
        assertNull(assembler.getOutputMessage(methodNoArgs, operation));
    }
}