package org.codehaus.xfire.plexus.config;

import org.codehaus.xfire.plexus.PlexusXFireTest;
import org.codehaus.xfire.service.object.ObjectService;
import org.dom4j.Document;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ConfigurationTest 
    extends PlexusXFireTest
{
    public void setUp()
        throws Exception
    {
        System.setProperty("xfire.config", "/org/codehaus/xfire/plexus/config/services.xml");
        super.setUp();
        
        lookup(ConfigurationService.ROLE);
    }
    
    public void testRegister()
        throws Exception
    {
        ObjectService service = (ObjectService) getServiceRegistry().getService("Echo");
        
        assertNotNull(service); 
        
        ObjectService xbService = (ObjectService) getServiceRegistry().getService("EchoXMLBeans");
        
        assertNotNull( xbService ); 
    }
    
    public void testInvoke() 
    	throws Exception
    {
        Document response = invokeService("Echo", "/org/codehaus/xfire/plexus/config/echo11.xml");
        
        addNamespace("e", "urn:Echo");
        assertValid("//e:out[text()='Yo Yo']", response);
        
        response = invokeService("EchoIntf", "/org/codehaus/xfire/plexus/config/echo11.xml");
        
        addNamespace("e", "urn:Echo");
        assertValid("//e:out[text()='Yo Yo']", response);
    }
}
