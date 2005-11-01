package org.codehaus.xfire.spring.config;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.spring.XFireConfigLoader;

import junit.framework.TestCase;

/**
 * @author tomeks
 *
 */
public class XFireConfigLoaderTest
    extends TestCase
{
    public void testConfigLoader()
        throws Exception
    {
        XFireConfigLoader loader = new XFireConfigLoader();
        XFire xfire = loader.loadConfig("META-INF/xfire/sservices.xml");
        assertNotNull(xfire);
        assertEquals(xfire.getInHandlers().size(),3);
        assertEquals(xfire.getOutHandlers().size(),1);
        assertEquals(xfire.getFaultHandlers().size(),1);
        Service service = xfire.getServiceRegistry().getService("testservice");
        assertNotNull(service);
        SoapVersion version = service.getSoapVersion();
        assertEquals(version,Soap12.getInstance());
        assertEquals(service.getInHandlers().size(),2);
        assertEquals(service.getProperty("myKey"),"value");
        assertEquals(service.getProperty("myKey1"),"value1");
        
    }
}
