package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class ElementRefTest
    extends GenerationTestSupport
{    
    public void testEchoWithFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoUser.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setBaseURI(getTestFilePath(""));
        generator.setDestinationPackage("org.codehaus.xfire.echo.ref");
        generator.generate();
    }
    
}
