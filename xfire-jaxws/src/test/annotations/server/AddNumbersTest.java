package annotations.server;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;

public class AddNumbersTest
    extends AbstractXFireAegisTest
{
    private Endpoint endpoint;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        endpoint = Endpoint.publish("http://localhost:8080/jaxws-fromjava/addnumbers", new AddNumbersImpl());
    }
    
    public void testService()
    {
        AddNumbersImplService jaxService = new AddNumbersImplService();

        assertNotNull(jaxService.getServiceName());
        
        Iterator<QName> names = jaxService.getPorts();
        assertTrue(names.hasNext());
        names.next();
        assertFalse(names.hasNext());
        
        AddNumbersIF p = jaxService.getPort(AddNumbersIF.class);
        
        
    }
    /*
    public void testPublish()
    {
        Endpoint.publish ("http://localhost:8080/jaxws-fromjava/addnumbers",
                          new AddNumbersImpl ());
    }*/
}
