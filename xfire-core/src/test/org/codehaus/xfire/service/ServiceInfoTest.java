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
        OperationInfo operationInfo = new OperationInfo("name");

        assertTrue(serviceInfo.getOperationInfos().isEmpty());
        serviceInfo.addOperationInfo(operationInfo);
        assertEquals(1, serviceInfo.getOperationInfos().size());
        OperationInfo result = serviceInfo.getOperationInfo(operationInfo.getName());
        assertNotNull(result);
        assertEquals(operationInfo, result);
    }
}