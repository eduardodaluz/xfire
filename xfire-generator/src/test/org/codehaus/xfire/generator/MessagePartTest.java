package org.codehaus.xfire.generator;


import org.codehaus.xfire.gen.Wsdl11Generator;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class MessagePartTest
    extends GenerationTestSupport
{
    public void testFault() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl(getTestFilePath("src/wsdl/concat.wsdl"));
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("org.codehaus.xfire.generator.messagepart");
        generator.setBinding("jaxb");
        generator.setBaseURI(getTestFilePath("src/wsdl/import-test/"));
        generator.setOverwrite(true);
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass concat = model._getClass("org.codehaus.xfire.generator.messagepart.concatPortType");
        assertNotNull(concat);
        
        JType string = model._ref(String.class);
        JMethod method = concat.getMethod("concat", new JType[] { string, string });
        
        // if there were a way to look at the annotations we would do that here.
    }
}
