package org.codehaus.xfire.plexus.config;

import org.codehaus.xfire.java.JavaService;
import org.codehaus.xfire.plexus.PlexusXFireTest;
import org.codehaus.xfire.service.Service;
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
    }
    
    public void testRegister()
        throws Exception
    {
        Service s = getServiceRegistry().getService("Echo");
        
        assertNotNull( s ); 
        
        JavaService js = (JavaService) getServiceRegistry().getService("Echo2");
        
        assertNotNull( js ); 
        
        Service xs = getServiceRegistry().getService("Echo3");
        
        assertNotNull( xs );
        assertNotNull( xs.getServiceHandler() );
        assertNotNull( xs.getWSDLWriter() );
    }
    
    public void testInvoke() 
    	throws Exception
    {
        Document response = invokeService("Echo2", "/org/codehaus/xfire/plexus/config/echo11.xml");
        
        addNamespace("e", "urn:Echo2");
        assertValid("//e:out[text()='Yo Yo']", response);
    }
}
