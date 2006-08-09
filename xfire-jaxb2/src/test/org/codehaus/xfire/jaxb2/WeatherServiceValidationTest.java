package org.codehaus.xfire.jaxb2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.jaxb2.WeatherService;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;



/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class WeatherServiceValidationTest extends AbstractXFireTest {

	private Service service;

	private ObjectServiceFactory factory;

	public void setUp() throws Exception {
		super.setUp();

		factory = new JaxbServiceFactory(getXFire().getTransportManager());
		factory.setStyle(SoapConstants.STYLE_DOCUMENT);

		// Set the schemas
		ArrayList<String> schemas = new ArrayList<String>();
		schemas.add(getTestFile("src/test-schemas/WeatherForecast.xsd")
				.getAbsolutePath());
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ObjectServiceFactory.SCHEMAS, schemas);

		service = factory.create(WeatherService.class, "WeatherService",
				"urn:WeatherService", props);

		service.setProperty(JaxbType.ENABLE_VALIDATION, "true");
		getServiceRegistry().register(service);
	}

	/**
	 * @throws Exception
	 */
	public void testService() throws Exception {

		Document response = invokeService("WeatherService",
				"GetWeatherByZip.xml");
		response = invokeService("WeatherService", "GetWeatherByZip.xml");

		addNamespace("w", "http://www.webservicex.net");
		assertValid(
				"//s:Body/w:GetWeatherByZipCodeResponse/w:GetWeatherByZipCodeResult",
				response);
	}
 
     public void testServiceNoSchema() throws Exception {

		service.setProperty(ObjectServiceFactory.SCHEMAS, null);
		Document response = invokeService("WeatherService",
				"GetWeatherByZip.xml");
		response = invokeService("WeatherService", "GetWeatherByZip.xml");

		addNamespace("w", "http://www.webservicex.net");
		assertValid(
				"//s:Body/w:GetWeatherByZipCodeResponse/w:GetWeatherByZipCodeResult",
				response);
	}
	
	
	/**
	 * @throws Exception
	 */
	public void testValidationFail() throws Exception {
		Collection schemas = new ArrayList();

		schemas.add(getTestFile("src/test-schemas/picture.xsd")
				.getAbsolutePath());
		service.setProperty(JaxbType.VALIDATION_SCHEMA, schemas);
	
		Document response = invokeService("WeatherService",
				"GetWeatherByZip.xml");
		
	     addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
	     addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
	     addNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
		 assertValid("//soap:Body/soap:Fault", response);
	
	}

}
