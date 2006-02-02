package jsr181;

import org.codehaus.xfire.gen.Wsdl11Generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class JaxbGenerationTest
    extends GenerationTestSupport
{
    public void testGlobalWeather() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/globalweather.wsdl"));
        generator.setOutputDirectory("target/test-services");
        generator.setDestinationPackage("jsr181.jaxb.globalweather");
        generator.setBinding("jaxb");
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.jaxb.globalweather.GlobalWeatherSoap");
        assertNotNull(echo);
    }

    public void testEchoWrappedServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoWrapped.wsdl"));
        generator.setOutputDirectory("target/test-services");
        generator.setDestinationPackage("jsr181.jaxb.echo.wrapped");
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.jaxb.echo.wrapped.EchoPortType");
        assertNotNull(echo);
        
        /*JMethod method = echo.getMethod("echo", new JType[] { model._ref(String.class) });
        assertNotNull(method);
        assertEquals( model.ref(String.class), method.type() );
        
        assertNotNull(model._getClass("jsr181.jaxb.echo.wrapped.EchoClient"));
        assertNotNull(model._getClass("jsr181.jaxb.echo.wrapped.EchoImpl"));*/
    }
}
