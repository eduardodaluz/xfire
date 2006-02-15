package org.codehaus.xfire.spring.examples;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.CustomTypeMapping;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.springframework.context.ApplicationContext;

public class CustomTypeTest
    extends AbstractXFireSpringTest
{
    protected ApplicationContext createContext()
    {
        return
            new ClassPathXmlApplicationContext(new String[] {
                "/org/codehaus/xfire/spring/examples/customType.xml", 
                "/org/codehaus/xfire/spring/xfire.xml" });
    }
    
    public void testService()
    {
        ServiceRegistry reg = (ServiceRegistry) getBean("xfire.serviceRegistry");
        assertTrue(reg.hasService("Echo"));
        
        Service service = reg.getService("Echo");
        OperationInfo operation = service.getServiceInfo().getOperation("echo");
        MessagePartInfo mp = (MessagePartInfo) operation.getInputMessage().getMessageParts().get(0);

        AegisBindingProvider bp = (AegisBindingProvider) getBean("xfire.aegisBindingProvider");
        
        TypeMapping dtypeMapping = bp.getTypeMappingRegistry().getDefaultTypeMapping();

        TypeMapping typeMapping = bp.getTypeMapping(service);
        assertEquals(((CustomTypeMapping)typeMapping).getParent(), dtypeMapping);
    }
    
    public void testClient()
    {
        // START SNIPPET: client 
        ObjectServiceFactory osf = new ObjectServiceFactory();
        AegisBindingProvider bp = (AegisBindingProvider) osf.getBindingProvider();
        
        bp.getTypeMappingRegistry().getDefaultTypeMapping().register(new CustomType());
        // END SNIPPET: client
    }
}