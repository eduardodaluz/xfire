package org.codehaus.xfire.service;


import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class VisitorTest
        extends TestCase
{
    public void testVisitor()
    {
        DefaultService service = new DefaultService();
        
        OperationInfo operation = new OperationInfo("test", service, null);
        service.addOperation(operation);
        MessageInfo inputMessage = operation.createMessage("input");
        operation.setInputMessage(inputMessage);
        MessageInfo outputMessage = operation.createMessage("output");
        operation.setOutputMessage(outputMessage);
        FaultInfo faultInfo = operation.addFault("fault");
        MessagePartInfo partInfo1 = inputMessage.addMessagePart(new QName("urn:test", "part1"), null);
        MessagePartInfo partInfo2 = inputMessage.addMessagePart(new QName("urn:test", "part2"), null);

        MockVisitor visitor = new MockVisitor();
        service.accept(visitor);

        assertTrue(visitor.visited(service));
        assertTrue(visitor.visited(operation));
        assertTrue(visitor.visited(inputMessage));
        assertTrue(visitor.visited(outputMessage));
        assertTrue(visitor.visited(faultInfo));
        assertTrue(visitor.visited(partInfo1));
        assertTrue(visitor.visited(partInfo2));
    }

    private class MockVisitor
            implements Visitor
    {
        private List visited = new ArrayList();

        public void visit(Service serviceInfo)
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
            assertFalse(messageInfo.getClass().equals(FaultInfo.class));
            visited.add(messageInfo);
        }

        public void visit(FaultInfo faultInfo)
        {
            assertNotNull(faultInfo);
            visited.add(faultInfo);
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