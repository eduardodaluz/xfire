package org.codehaus.xfire.spring.config;


import javax.xml.namespace.QName;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.codehaus.xfire.spring.ServiceBean;
import org.springframework.context.ApplicationContext;

/**
 * @author tomeks
 *
 */
public class OperationMetadataTest
    extends AbstractXFireSpringTest
{
    public void testBeans()
    {
      
        ServiceBean serviceBean = (ServiceBean) getBean("ConcatService");
        assertEquals(2, serviceBean.getMethods().size());

        Service service = serviceBean.getXFireService();
        OperationInfo opInfo = service.getServiceInfo().getOperation("concat");
        assertNotNull(opInfo);
        
        assertEquals(2, opInfo.getInputMessage().size());

        MessagePartInfo part = opInfo.getInputMessage().getMessagePart(new QName("urn:concat-service", "s1"));
        assertNotNull(part);
        
        opInfo = service.getServiceInfo().getOperation("concatThreeStrings");
        assertNotNull(opInfo);
        
        part = opInfo.getInputMessage().getMessagePart(new QName("urn:test", "two"));
        assertNotNull(part);
        
        part = opInfo.getOutputMessage().getMessagePart(new QName("urn:test", "sum"));
        assertNotNull(part);
        
        opInfo = service.getServiceInfo().getOperation("excluded");
        assertNull(opInfo);
    }
    
    protected ApplicationContext createContext()
    {
        return new ClassPathXmlApplicationContext(new String[] {
                "org/codehaus/xfire/spring/xfire.xml", "/org/codehaus/xfire/spring/config/OperationMetadataServices.xml" });
    }
}
