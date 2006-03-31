package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class ServiceInterfaceGeneratorTest
    extends GenerationTestSupport
{
    public void testEchoServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echo.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.echo.bare");
        generator.setBinding(Wsdl11Generator.XMLBEANS);
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        assertNotNull(model._getClass("jsr181.echo.bare.EchoPortType"));
        assertNotNull(model._getClass("jsr181.echo.bare.EchoClient"));
        assertNotNull(model._getClass("jsr181.echo.bare.EchoImpl"));
    }
    
    public void testEchoWrappedServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoWrapped.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.echo.wrapped");
        generator.setBinding(Wsdl11Generator.XMLBEANS);
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.echo.wrapped.EchoPortType");
        assertNotNull(echo);
        
        JMethod method = echo.getMethod("echo", new JType[] { model.ref(String.class) });
        assertNotNull(method);
        assertEquals( model.ref(String.class), method.type() );
        
        assertNotNull(model._getClass("jsr181.echo.wrapped.EchoClient"));
        assertNotNull(model._getClass("jsr181.echo.wrapped.EchoImpl"));
    }
    
    public void testGlobalWeatherTwoSoapPortTypes() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/globalweather-twoporttypes.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.globalweather.twopts");
        generator.setBinding(Wsdl11Generator.XMLBEANS);
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.globalweather.twopts.GlobalWeatherSoap");
        assertNotNull(echo);
        echo = model._getClass("jsr181.globalweather.twopts.GlobalWeatherSoap2");
        assertNotNull(echo);
    }
    
    public void testGlobalWeather() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/globalweather.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.globalweather.withhttp");
        generator.setBinding(Wsdl11Generator.XMLBEANS);
        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.globalweather.withhttp.GlobalWeatherSoap");
        assertNotNull(echo);
        echo = model._getClass("jsr181.globalweather.withhttp.GlobalWeatherHttpGet");
        assertNull(echo);
    }
}
