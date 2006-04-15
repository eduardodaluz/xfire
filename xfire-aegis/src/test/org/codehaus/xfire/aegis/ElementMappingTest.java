package org.codehaus.xfire.aegis;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.services.AddNumbers;

public class ElementMappingTest extends AbstractXFireAegisTest
{
    public void testMapping() 
    {
        String ns = "urn:AddNumbers";
        Service service = getServiceFactory().create(AddNumbers.class, "AddNumbers", ns, null);
        
        OperationInfo operation = service.getServiceInfo().getOperation("add");
        
        MessagePartInfo v1 = operation.getInputMessage().getMessagePart(new QName(ns, "value1"));
        assertNotNull(v1);
        
        MessagePartInfo v2 = operation.getInputMessage().getMessagePart(new QName(ns, "value2"));
        assertNotNull(v2);
        
        MessagePartInfo sum = operation.getOutputMessage().getMessagePart(new QName(ns, "sum"));
        assertNotNull(sum);
    }
}
