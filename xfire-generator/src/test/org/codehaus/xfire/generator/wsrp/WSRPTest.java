package org.codehaus.xfire.generator.wsrp;


import org.codehaus.xfire.gen.Wsdl11Generator;
import org.codehaus.xfire.generator.GenerationTestSupport;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class WSRPTest
    extends GenerationTestSupport
{
    public void testWSRP() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl("src/wsdl/wsrp/wsrp_service.wsdl");
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("org.codehaus.xfire.generator.wsrp");
        generator.setBinding("jaxb");
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("org.codehaus.xfire.generator.wsrp.WSRPServiceClient");
        assertNotNull(echo);
    }
    
    public void testAdmin() throws Exception
    {
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setWsdl("http://www.rakis.com/webservices/47/definitions/admin-main.wsdl");
        generator.setOutputDirectory(getTestFilePath("target/test-services"));
        generator.setDestinationPackage("org.codehaus.xfire.generator.wsrp");
        generator.setBinding("jaxb");
        generator.generate();
        
        JCodeModel model = generator.getCodeModel();
        JDefinedClass echo = model._getClass("org.codehaus.xfire.generator.wsrp.WSRPServiceClient");
        assertNotNull(echo);
    }
}
