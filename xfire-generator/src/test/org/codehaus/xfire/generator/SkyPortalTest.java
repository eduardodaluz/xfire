package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

public class SkyPortalTest
    extends GenerationTestSupport
{
    public void testEchoServiceIntf() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/SkyPortal.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("org.codehaus.xfire.generator.skyportal");
        generator.setOverwrite(true);
        
        // TODO reenable once JAXB 2.0.3 is out
        // generator.generate();        
    }
}
