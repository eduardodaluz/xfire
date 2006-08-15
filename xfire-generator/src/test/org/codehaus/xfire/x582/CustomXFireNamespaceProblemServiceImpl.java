
package org.codehaus.xfire.x582;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.bt.test._2006._08.service.schema.Name;

@WebService(serviceName = "XFireNamespaceProblemService", targetNamespace = "http://test.bt.com/2006/08/Service", endpointInterface = "org.codehaus.xfire.x582.XFireNamespaceProblemInterface")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class CustomXFireNamespaceProblemServiceImpl
    implements XFireNamespaceProblemInterface
{


    public String makeCall(Name name) {
        return "foo";
    }

}
