package org.codehaus.xfire.jaxws.gen;

import org.codehaus.xfire.gen.Wsdl11Generator;
import org.codehaus.xfire.jaxws.AbstractJAXWSTest;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class JaxbGenerationTest
    extends AbstractJAXWSTest
{
    public void testEcho() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl("src/wsdl/echo.wsdl");
        generator.setOutputDirectory("target/test-services");
        generator.setDestinationPackage("services.echo");
        generator.setProfile(JAXWSProfile.class.getName());
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("services.echo.EchoPortType");
        assertNotNull(echo);
    }

}
