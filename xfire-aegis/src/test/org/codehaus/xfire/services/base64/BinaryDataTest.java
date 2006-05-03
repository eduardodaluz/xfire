package org.codehaus.xfire.services.base64;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.jdom.Document;

public class BinaryDataTest extends AbstractXFireAegisTest
{
    public void testBinary() throws Exception 
    {
        Service service = getServiceFactory().create(BinaryDataService.class);
        getServiceRegistry().register(service);
        
        runTests();
    }
    
    public void testBinaryWithDOM() throws Exception 
    {
        Service service = getServiceFactory().create(BinaryDataService.class);
        service.addInHandler(new DOMInHandler());
        getServiceRegistry().register(service);
        
        runTests();
    }
    
    private void runTests()
        throws Exception
    {
        Document res = invokeService("BinaryDataService", "/org/codehaus/xfire/services/base64/binary.xml");
        
        addNamespace("b", "http://base64.services.xfire.codehaus.org");
        assertValid("//b:out[text()='OK']", res);
        
        res = invokeService("BinaryDataService", "/org/codehaus/xfire/services/base64/binaryEmpty.xml");
        assertValid("//b:out[text()='OK']", res);
    }

}
