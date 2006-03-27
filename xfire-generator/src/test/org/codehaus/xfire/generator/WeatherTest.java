package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class WeatherTest
    extends GenerationTestSupport
{
    public void testEchoServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/WeatherForecast.wsdl"));
        generator.setOutputDirectory("target/gen/echo");
        generator.setDestinationPackage("weather");

        generator.generate();        
    }
}
