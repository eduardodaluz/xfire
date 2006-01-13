package org.codehaus.xfire.aegis.type.basic;

import java.util.List;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Document;

public class MethodReturnMappingTest extends AbstractXFireAegisTest {

	public void setUp() throws Exception {
		super.setUp();
		Service endpoint = getServiceFactory().create(RenamedBeanService.class);
		getServiceRegistry().register(endpoint);
	}

	public void testWSDL() throws Exception {
		Document doc = getWSDLDocument("RenamedBeanService");

		printNode(doc);

		addNamespace("xsd", SoapConstants.XSD);
		assertValid("//xsd:complexType[@name='NewBean']", doc);
	}

	public static class RenamedBeanService {
		public List getBeanList() {
			return null;
		}
	}
}
