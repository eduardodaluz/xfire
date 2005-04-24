package org.codehaus.xfire.service;

/**
 * @author Arjen Poutsma
 */

import junit.framework.TestCase;

public class ServiceInfoTest
        extends TestCase
{
    private ServiceInfo serviceInfo;

    protected void setUp()
            throws Exception
    {
        serviceInfo = new ServiceInfo();
    }

    public void testOperations()
            throws Exception
    {
        OperationInfo operationInfo = serviceInfo.addOperation("name");

        assertEquals(1, serviceInfo.getOperations().size());
        OperationInfo result = serviceInfo.getOperation(operationInfo.getName());
        assertNotNull(result);
        assertEquals(operationInfo, result);
    }
}