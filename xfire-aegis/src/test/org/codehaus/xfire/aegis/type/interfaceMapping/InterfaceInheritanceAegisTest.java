package org.codehaus.xfire.aegis.type.interfaceMapping;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.jdom.Document;

public class InterfaceInheritanceAegisTest extends AbstractXFireAegisTest {

	private Service service;

	public void setUp() throws Exception {
		super.setUp();

		service = getServiceFactory().create(InterfaceService.class,
                "InterfaceService",
                "urn:InterfaceService",
                null);
		service.setInvoker(new BeanInvoker(new InterfaceTestService()));
		getServiceRegistry().register(service);
	}

	public void testGetStrings() throws Exception {

		Document doc = getWSDLDocument("InterfaceService"); 
		//printNode(doc);
		addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
		assertValid("//xsd:complexType[@name=\"BeanImpl\"]/xsd:sequence/xsd:element[@name=\"exposedName\"]",doc);
		assertValid("//xsd:complexType[@name=\"BeanImpl\"]/xsd:sequence/xsd:element[@name=\"exposedParentName\"]",doc);
	}

}
