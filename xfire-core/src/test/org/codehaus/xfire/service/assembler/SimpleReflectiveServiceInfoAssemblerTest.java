package org.codehaus.xfire.service.assembler;

/**
 * @author Arjen Poutsma
 */

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.ServiceInfo;

public class SimpleReflectiveServiceInfoAssemblerTest
        extends TestCase
{
    private SimpleReflectiveServiceInfoAssembler assembler;

    protected void setUp()
            throws Exception
    {
        assembler = new SimpleReflectiveServiceInfoAssembler();
    }


    public void testPopulateServiceInfo()
            throws Exception
    {
        ServiceInfo serviceInfo = new ServiceInfo();
        assembler.populate(serviceInfo, getClass());
        assertEquals("SimpleReflectiveServiceInfoAssemblerTest", serviceInfo.getName());
        assertEquals("http://assembler.service.xfire.codehaus.org", serviceInfo.getNamespace());
    }

    public void methodWithArgs(String s)
    {
        // this method is here for the testPopulateOperationInfo() test
    }

    public void methodWithNoArgs()
    {
        // this method is here for the testPopulateOperationInfoOneWay() test
    }

    public void testPopulateOperationNotOneWay()
            throws Exception
    {
        Method argsMethod = getClass().getMethod("methodWithArgs", new Class[]{String.class});
        OperationInfo operationInfo = new OperationInfo("methodWithArgs");
        assembler.populate(operationInfo, argsMethod);
        assertEquals("methodWithArgs", operationInfo.getName());
        assertFalse(operationInfo.isOneWay());
    }

    public void testPopulateOperationInfoOneWay()
            throws Exception
    {
        Method argsMethod = getClass().getMethod("methodWithNoArgs", new Class[0]);
        OperationInfo operationInfo = new OperationInfo("methodWithNoArgs");
        assembler.populate(operationInfo, argsMethod);
        assertEquals("methodWithNoArgs", operationInfo.getName());
        assertTrue(operationInfo.isOneWay());
    }
}