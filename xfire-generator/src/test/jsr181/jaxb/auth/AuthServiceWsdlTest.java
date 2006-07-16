package jsr181.jaxb.auth;

import java.io.File;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.XFireConfigLoader;
import org.codehaus.xfire.wsdl.ResourceWSDL;

public class AuthServiceWsdlTest   
    extends AbstractXFireAegisTest
{
    public void testWSDL() throws Exception
    {   
       XFireConfigLoader loader = new XFireConfigLoader();
       XFire xfire = loader.loadConfig(new File(new File(getBasedir()), "target/auth-service/META-INF/xfire/services.xml").getAbsolutePath());
       
       assertTrue(xfire.getServiceRegistry().getServices().size() == 1);
       System.out.println(((Service)xfire.getServiceRegistry().getServices().iterator().next()).getName());
       Service service = xfire.getServiceRegistry().getService(new QName("urn:xfire:authenticate", "AuthService"));
       
       assertNotNull(service);
       
       assertTrue(service.getWSDLWriter() instanceof ResourceWSDL);
    }
}
