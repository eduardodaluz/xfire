package org.codehaus.xfire.service.assembler;

/**
 * @author Arjen Poutsma
 */

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class ReflectiveServiceInfoAssemblerTest
        extends TestCase
{
    public void testPopulateServiceInfoServiceInfo()
            throws Exception
    {
        ReflectiveServiceInfoAssembler assembler = new ReflectiveServiceInfoAssembler(getClass())
        {

            protected void populateServiceInfo(ServiceInfo serviceInfo, Class serviceClass)
            {
                assertEquals(ReflectiveServiceInfoAssemblerTest.class, serviceClass);
                assertEquals("ReflectiveServiceInfoAssemblerTest", serviceInfo.getName());
                assertEquals("http://assembler.service.xfire.codehaus.org", serviceInfo.getNamespace());
            }

        };
        assembler.populateServiceInfo(new ServiceInfo());
    }

    public String methodWithArgs(String s)
    {
        // this method is here for the tests
        return s;
    }

    public void methodWithNoArgs()
    {
        // this method is here for the tests
    }

    public void testPopulateServiceInfoOperationInfo()
            throws Exception
    {
        ReflectiveServiceInfoAssembler assembler = new ReflectiveServiceInfoAssembler(getClass())
        {
            protected Method[] getOperationMethods(Class serviceClass)
            {
                try
                {

                    Method argsMethod = ReflectiveServiceInfoAssemblerTest.class.getMethod("methodWithArgs",
                                                                                           new Class[]{String.class});
                    Method noArgsMethod = ReflectiveServiceInfoAssemblerTest.class.getMethod("methodWithNoArgs",
                                                                                             new Class[0]);
                    return new Method[]{argsMethod, noArgsMethod};
                }
                catch (NoSuchMethodException e)
                {
                    fail(e.getMessage());
                    return new Method[0];
                }

            }

            protected void populateOperationInfo(OperationInfo operationInfo, Method method)
            {
                if (method.getName().equals("methodWithArgs"))
                {
                    assertEquals("methodWithArgs", operationInfo.getName());
                    assertFalse(operationInfo.isOneWay());

                }
                else if (method.getName().equals("methodWithNoArgs"))
                {
                    assertEquals("methodWithNoArgs", operationInfo.getName());
                    assertTrue(operationInfo.isOneWay());

                }
                else
                {
                    fail("Invalid method passed to populate: [" + method.getName() + "]");
                }
            }


        };
        assembler.populateServiceInfo(new ServiceInfo());
    }

    public void testGetOperationMethods()
            throws Exception
    {
        Object testObject = new Object()
        {
            public void publicMethod()
            {
            }

            private void privateMethod()
            {
            }

            protected void protectedMethod()
            {
            }
        };
        ReflectiveServiceInfoAssembler assembler = new ReflectiveServiceInfoAssembler(testObject.getClass());
        Method[] methods = assembler.getOperationMethods(testObject.getClass());
        assertNotNull(methods);
        assertEquals(1, methods.length);
        assertEquals("publicMethod", methods[0].getName());
    }

    public void testGetNoOutputMessagePart()
            throws Exception
    {
        Method noArgsMethod = ReflectiveServiceInfoAssemblerTest.class.getMethod("methodWithNoArgs",
                                                                                 new Class[0]);
        ReflectiveServiceInfoAssembler assembler = new ReflectiveServiceInfoAssembler(getClass());
        OperationInfo operation = new ServiceInfo().addOperation("operation");
        assembler.populateOutputMessage(noArgsMethod, operation);
        assertNull(operation.getOutputMessage());
    }

    public void testPopulateOutputMessagePart()
            throws Exception
    {
        Method argsMethod = ReflectiveServiceInfoAssemblerTest.class.getMethod("methodWithArgs",
                                                                               new Class[]{String.class});
        ReflectiveServiceInfoAssembler assembler = new ReflectiveServiceInfoAssembler(getClass());
        ServiceInfo service = new ServiceInfo();
        service.setNamespace("namespace");
        OperationInfo operation = service.addOperation("operation");
        assembler.populateOutputMessage(argsMethod, operation);
        MessageInfo outputMessage = operation.getOutputMessage();
        assertNotNull(outputMessage);
        assertEquals("methodWithArgsResponse", outputMessage.getName());
        assertEquals("namespace", outputMessage.getNamespace());
        assertNotNull(outputMessage.getMessagePart("methodWithArgsResponseout"));


    }
}