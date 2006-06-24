package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class FaultTest
    extends GenerationTestSupport
{
    public void testFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoFault.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.jaxb.echofault");
        generator.setForceOverwrite(true);        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.jaxb.echofault.EchoWithFaultPortType");
        assertNotNull(echo);
    }

}
