package ${groupId};

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class SimpleServiceImpl {

	@WebMethod
	public String echoMethod(String value) {
		return value;
	}
}
