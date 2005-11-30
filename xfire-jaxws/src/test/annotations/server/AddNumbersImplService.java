package annotations.server;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.soap.SOAPBinding;

@WebServiceClient(name="AddNumbersService", targetNamespace="", wsdlLocation="")
public class AddNumbersImplService
    extends Service
{
    public static final Class SERVICE_CLASS = AddNumbersIF.class;
    private QName addNumbersImplPort = new QName("http://duke.org", "AddNumbersPort");
    
    public AddNumbersImplService()
    {
        super(null, new QName("http://duke.org", "AddNumbers"));

        try
        {
            addPort(addNumbersImplPort, 
                    new URI(SOAPBinding.SOAP11HTTP_BINDING), 
                    "http://localhost/AddNumbers");
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @WebEndpoint(name="AddNumbersPort")
    public AddNumbersIF getAddNumbersImplPort()
    {
        return (AddNumbersIF) getPort(addNumbersImplPort, SERVICE_CLASS);
    }
}
