package org.codehaus.xfire.jaxws.type;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.jaxws.JAXWSServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.LoggingHandler;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.jdom.Document;

public class CollectionTest extends AbstractXFireAegisTest {

	public void testWSDL() throws Exception {
		JaxbServiceFactory sf = new JaxbServiceFactory();
		
		Service service = sf.create(CollectionServiceImpl.class, "CollectionService", null, null);
		getServiceRegistry().register(service);
		
		service.addOutHandler(new LoggingHandler());
		service.addOutHandler(new DOMOutHandler());
		
	    Document d = getWSDLDocument("CollectionService");

	    CollectionService client = (CollectionService)
	    	new XFireProxyFactory(getXFire()).create(service, 
	    			"xfire.local://CollectionService");
	    
	    client.getFoos();
	}
}
