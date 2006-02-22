package org.codehaus.xfire.security.wssecurity;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.spring.XFireConfigLoader;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;


public class WSS4JEncTest
    extends AbstractXFireTest
{

    //private XFire xfire = null;
    
    public void testInEncryption() throws Exception{
        
        Document doc = invokeService("echo",
        "/org/codehaus/xfire/security/wssecurity/in_enc.xml");
        
        
    
    }
    /*protected XFire getXFire()
    {
        XFireConfigLoader loader = new XFireConfigLoader();
        xfire = loader.loadConfig("",new String[]{"META-INF/xfire/service_enc.xml"});
        return xfire;
    }
    
*/    
}
