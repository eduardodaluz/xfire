package org.codehaus.xfire.demo;
import org.codehaus.xfire.plexus.PlexusXFireTest;
import org.codehaus.xfire.plexus.config.ConfigurationService;
import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DemoServiceTest
    extends PlexusXFireTest
{
    public void setUp() throws Exception
    {
        System.setProperty("xfire.config", 
                           getTestFile("resources/WEB-INF/services.xml").getAbsolutePath());
        
        super.setUp();
    }
    
    public void testServices() throws Exception
    {
        lookup(ConfigurationService.ROLE);
        
        Service echo = getServiceRegistry().getService("Echo");
        assertNotNull(echo);
        
        Service book = getServiceRegistry().getService("BookService");
        assertNotNull(echo);
    }
}
