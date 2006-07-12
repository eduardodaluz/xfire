package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class HeaderTest
    extends GenerationTestSupport
{    
    public void testEchoWithFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoHeader.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setBaseURI(getTestFilePath(""));
        generator.setDestinationPackage("org.codehaus.xfire.echo.header");
        generator.setOverwrite(true);
        generator.generate();
    }
    
}
