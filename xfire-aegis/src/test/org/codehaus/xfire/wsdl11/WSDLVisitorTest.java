package org.codehaus.xfire.wsdl11;

import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.type.AutoTypeMapping;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.basic.StringType;
import org.codehaus.xfire.service.binding.ObjectService;
import org.codehaus.xfire.service.binding.Operation;
import org.codehaus.xfire.services.Echo;
import org.codehaus.yom.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WSDLVisitorTest
    extends AbstractXFireAegisTest
{
    public void testVisitor()
        throws Exception
    {
        /*TypeMapping tm = new AutoTypeMapping();
        tm.register(String.class, new QName("urn:Echo", "echoRequest"), new StringType());
        tm.register(String.class, new QName("urn:Echo", "echoResponse"), new StringType());
        
        ObjectService service = 
            (ObjectService) getServiceBuilder()
                .create(Echo.class, tm, getTestFile("src/test/org/codehaus/xfire/wsdl11/Echo.wsdl").toURL());

        getXFire().getServiceRegistry().register( service );

        assertEquals(1, service.getOperations().size());
        
        Operation o = service.getOperation("echo");
        assertNotNull(o);
        
        Document response = invokeService("Echo", "/org/codehaus/xfire/wsdl11/echo11.xml");
        
        addNamespace("e", "urn:Echo");
        assertValid("//e:echoResponse[text()='Yo Yo']", response);*/
    }
}
