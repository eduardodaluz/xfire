package jsr181.jaxb.auth;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;

public class AuthServiceWsdlTest   
    extends AbstractXFireAegisTest
{
    public void testWSDL() throws Exception
    {   
       JaxbServiceFactory asf = new JaxbServiceFactory(getXFire().getTransportManager());
       Service service = asf.create(jsr181.jaxb.auth.AuthServiceImpl.class);
        
       WSDLWriter writer = service.getWSDLWriter();
       assertTrue(writer instanceof ResourceWSDL);
    }
}
