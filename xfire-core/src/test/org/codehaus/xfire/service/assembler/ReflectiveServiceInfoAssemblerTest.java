package org.codehaus.xfire.service.assembler;

/**
 * @author Arjen Poutsma
 */

import java.lang.reflect.Method;

import junit.framework.TestCase;
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
        assembler.populate(new ServiceInfo());
    }

    public void methodWithArgs(String s)
    {
        // this method is here for the testPopulateServiceInfoOperationInfo() test
    }

    public void methodWithNoArgs()
    {
        // this method is here for the testPopulateOperationInfoOneWay() test
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
        assembler.populate(new ServiceInfo());
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
}