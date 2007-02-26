package org.codehaus.xfire.jaxb2;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;

import xfire.inheritance.BaseUser;
import xfire.inheritance2.Employee;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class InheritanceTest
        extends AbstractXFireTest
{
    private Service endpoint;
    private ObjectServiceFactory builder;

    public void setUp()
            throws Exception
    {
        super.setUp();

        builder = new ObjectServiceFactory(getXFire().getTransportManager(),
                                           new AegisBindingProvider(new JaxbTypeRegistry()));

        
        endpoint = builder.create(InheritanceService.class,
                                  "InheritanceService",
                                  "urn:xfire:inheritance",
                                  null);
        endpoint.setInvoker(new BeanInvoker(new InheritanceServiceImpl()));
        
        List<String> pckgs = new ArrayList<String>();
        pckgs.add("xfire.inheritance2");
        endpoint.setProperty(JaxbType.SEARCH_PACKAGES, pckgs);
        getServiceRegistry().register(endpoint);
    }

    public void testService()
            throws Exception
    {
        Document response = invokeService("InheritanceService", "GetEmployee.xml");

        addNamespace("i", "urn:xfire:inheritance");
        addNamespace("i2", "urn:xfire:inheritance2");
        assertValid("//s:Body/i:getEmployeeResponse/i:out/i2:division", response);
        
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");
        response = invokeService("InheritanceService", "ReceiveEmployee.xml");

        addNamespace("w", "urn:xfire:inheritance");
        assertValid("//s:Body/w:receiveUserResponse", response);
    }
    
    public void testClient() throws Exception {
        InheritanceService client = (InheritanceService) 
        new XFireProxyFactory(getXFire()).create(endpoint, "xfire.local://InheritanceService");
        
        Client xc = Client.getInstance(client);

//        xc.addOutHandler(new LoggingHandler());
//        xc.addOutHandler(new DOMOutHandler());
//        xc.addInHandler(new LoggingHandler());
//        xc.addInHandler(new DOMInHandler());
        
        
        BaseUser employee = client.getEmployee();
        assertTrue(employee instanceof Employee);
        
        client.receiveUser(employee);
    }
    
    public static interface InheritanceService {
        public BaseUser getEmployee();
        public void receiveUser(BaseUser user);
    }
    
    public static class InheritanceServiceImpl implements InheritanceService {
        public BaseUser getEmployee() {
            Employee e = new Employee();
            e.setDivision("foo");
            return e;
        }
        
        public void receiveUser(BaseUser user) {
            assertTrue(user instanceof Employee);
        }
    }
}
