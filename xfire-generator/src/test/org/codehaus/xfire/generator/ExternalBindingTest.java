package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class ExternalBindingTest
    extends GenerationTestSupport
{    
    public void testEchoWithFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoFault.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setBaseURI(getTestFilePath(""));
        generator.setDestinationPackage("org.codehaus.xfire.echo.external");
        generator.setExternalBindings("src/bindings/echo_schema_import.xjb");
        generator.setOverwrite(true);
        generator.generate();

        assertTrue(getTestFile("target/test-services/org/codehaus/xfire/echo/external/Echo.java").exists());
    }
    
}
