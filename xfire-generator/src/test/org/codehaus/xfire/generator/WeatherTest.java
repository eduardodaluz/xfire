package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class WeatherTest
    extends GenerationTestSupport
{
    public void testEchoServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/WeatherForecast.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("weather");
        generator.setForceOverwrite(true);
        
        generator.generate();        
    }
}
