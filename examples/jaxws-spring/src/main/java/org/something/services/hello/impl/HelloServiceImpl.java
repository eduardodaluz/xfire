package org.something.services.hello.impl;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.something.services.hello.HelloRequest;
import org.something.services.hello.HelloResponse;
import org.something.services.hello.ws.HelloFaultMessage;
import org.something.services.hello.ws.HelloService;

/**
 * Hello Service Implementation. 
 * @author Michael Vorburger
 */
@WebService(serviceName = "HelloService", targetNamespace = "http://services.something.org/hello", endpointInterface = "org.something.services.hello.ws.HelloService")
//// wsdlLocation = "file:/C:/MyCode/Visana1/xfire-examples-fullmonty/src/wsdl/hello.wsdl"
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class HelloServiceImpl implements HelloService {

	/**
	 * Say "Hello {request.toWho}", or return Fault if toWho == "FAULT".
	 */
	public HelloResponse sayHello(HelloRequest helloRequest) throws HelloFaultMessage {
		if ("fault".equalsIgnoreCase(helloRequest.getToWho())) {

			org.something.services.hello.HelloFault fault = 
				new org.something.services.hello.HelloFault();
			fault.setShortErrorMessage("SomeThingsAreVeryWrong");
			fault.setTechImplementationDetails(new NullPointerException().toString());
			throw new HelloFaultMessage("Failure", fault);
		}
		else {
			HelloResponse response = new HelloResponse();
			response.setGreeting("Hello " + helloRequest.getToWho());
			return response;
		}
	}
}
