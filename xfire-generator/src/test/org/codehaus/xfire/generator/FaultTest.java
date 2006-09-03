package org.codehaus.xfire.generator;

import org.codehaus.xfire.gen.Wsdl11Generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class FaultTest
    extends GenerationTestSupport
{
    public void testHelloFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/hello/hello.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setOverwrite(true);        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass c = model._getClass("org.something.services.hello.HelloFaultMessage");
        assertNotNull(c);
        c = model._getClass("org.something.services.hello.HelloFault");
        assertNotNull(c);
    }
    
    public void testFaults() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/echoFault.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("jsr181.jaxb.echofault");
        generator.setOverwrite(true);        
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("jsr181.jaxb.echofault.EchoWithFaultPortType");
        assertNotNull(echo);
    }

}
