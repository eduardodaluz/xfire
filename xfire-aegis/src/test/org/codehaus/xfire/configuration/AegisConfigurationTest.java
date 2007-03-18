package org.codehaus.xfire.configuration;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.AegisServiceConfiguration;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Document;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class AegisConfigurationTest extends AbstractXFireAegisTest {

	public void testServiceConfig() throws Exception {
		ObjectServiceFactory factory = (ObjectServiceFactory) getServiceFactory();
		AegisServiceConfiguration conf = new AegisServiceConfiguration();
		conf.setServiceFactory(factory);
		factory.getServiceConfigurations().add(0, conf);
		Service service = getServiceFactory().create(EchoImpl.class);
        service.setInvoker(new BeanInvoker(new EchoImpl()));
        getServiceRegistry().register(service);
        Document wsdl = getWSDLDocument("EchoImpl");
        printNode(wsdl);
        addNamespace("wsdl", WSDLWriter.WSDL11_NS);
        addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
        addNamespace("wsdlsoap","http://schemas.xmlsoap.org/wsdl/soap/");
        
        assertValid("//xsd:element[@name='parameter_1']", wsdl);
        assertValid("//xsd:element[@name='parameter_2']", wsdl);
        assertValid("//wsdl:operation[@name='GetEchoMsg']", wsdl);
        assertValid("//wsdlsoap:operation[@soapAction='getEchoAction']", wsdl);
         
        
        
	}
}
