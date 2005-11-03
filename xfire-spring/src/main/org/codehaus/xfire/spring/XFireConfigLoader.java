package org.codehaus.xfire.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.xbean.spring.context.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class XFireConfigLoader
{
    private Log log = LogFactory.getLog(XFireConfigLoader.class);
    
    public XFire loadConfig(String configFile)
    {

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {
                "org/codehaus/xfire/spring/xfire.xml", configFile });

        XFire xfire = (XFire) ctx.getBean("xfire");
        log.error("Setting xfire :"+xfire);
        // TODO: don't like this
        XFireFactory.newInstance().setXFire(xfire);
        System.out.print("Xfire instance set \n"+xfire);
        return xfire;
    }

}
