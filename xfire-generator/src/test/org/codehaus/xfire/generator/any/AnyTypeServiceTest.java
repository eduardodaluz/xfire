package org.codehaus.xfire.generator.any;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.jaxb2.JaxbType;
import org.codehaus.xfire.service.Service;

import c.b.a._2006._07.authentication.AuthenticationInterface;
import c.b.a._2006._07.authentication.AuthenticationServiceClient;

public class AnyTypeServiceTest extends AbstractXFireAegisTest {
	public void testAnyTypeService() {
		Service service = new JaxbServiceFactory().create(AuthenticationServiceImpl.class);
		
		List<String> search = new ArrayList<String>();
		search.add("org.codehaus.xfire.generator.any");
		service.setProperty(JaxbType.SEARCH_PACKAGES, search);
		
		getServiceRegistry().register(service);
		
		AuthenticationServiceClient stub = new AuthenticationServiceClient();
		AuthenticationInterface client = stub.getAuthenticationInterfaceLocalEndpoint();
		
		Client xclient = Client.getInstance(client);
		xclient.setProperty(JaxbType.SEARCH_PACKAGES, search);
		 
		Object object = client.login("dan", "dan");
		assertEquals("hello", object);
		
		//object = client.login("foo", "dan");
		//assertTrue(object instanceof FooType);
	}

    @Override
    protected XFire getXFire()
    {
        return XFireFactory.newInstance().getXFire();
    }
    
}
