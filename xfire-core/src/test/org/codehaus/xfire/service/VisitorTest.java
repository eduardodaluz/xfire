package org.codehaus.xfire.service;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class VisitorTest
        extends TestCase
{
    public void testVisitor()
    {
        ServiceInfo serviceInfo = new ServiceInfo();
        OperationInfo operationInfo = new OperationInfo("operation");
        serviceInfo.addOperationInfo(operationInfo);
        MessageInfo inputMessageInfo = new MessageInfo("input");
        operationInfo.setInputMessageInfo(inputMessageInfo);
        MessageInfo outputMessageInfo = new MessageInfo("output");
        operationInfo.setOutputMessageInfo(outputMessageInfo);
        FaultInfo faultInfo = new FaultInfo("fault");
        operationInfo.addFaultInfo(faultInfo);
        MessagePartInfo partInfo1 = new MessagePartInfo("part1");
        inputMessageInfo.addMethodPartInfo(partInfo1);
        MessagePartInfo partInfo2 = new MessagePartInfo("part2");
        inputMessageInfo.addMethodPartInfo(partInfo2);

        MockVisitor visitor = new MockVisitor();
        serviceInfo.accept(visitor);

        assertTrue(visitor.visited(serviceInfo));
        assertTrue(visitor.visited(operationInfo));
        assertTrue(visitor.visited(inputMessageInfo));
        assertTrue(visitor.visited(outputMessageInfo));
        assertTrue(visitor.visited(faultInfo));
        assertTrue(visitor.visited(partInfo1));
        assertTrue(visitor.visited(partInfo2));
    }

    private class MockVisitor
            implements Visitor
    {
        private List visited = new ArrayList();

        public void visit(ServiceInfo serviceInfo)
        {
            assertNotNull(serviceInfo);
            visited.add(serviceInfo);
        }

        public void visit(OperationInfo operationInfo)
        {
            assertNotNull(operationInfo);
            visited.add(operationInfo);
        }

        public void visit(MessageInfo messageInfo)
        {
            assertNotNull(messageInfo);
            visited.add(messageInfo);
        }

        public void visit(MessagePartInfo messagePartInfo)
        {
            assertNotNull(visited);
            visited.add(messagePartInfo);
        }

        public boolean visited(Visitable visitable)
        {
            return visited.contains(visitable);
        }
    }


}