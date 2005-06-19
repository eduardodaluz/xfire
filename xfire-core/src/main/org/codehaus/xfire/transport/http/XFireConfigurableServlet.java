package org.codehaus.xfire.transport.http;

import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.util.XMLServiceBuilder;

/**
 * XFire Servlet as Dispatcher including a configuration<br>
 * of XFire from services.xml in classpath<br>
 * <p>
 */
public class XFireConfigurableServlet
    extends XFireServlet
{

    private final static String CONFIG_FILE = "META-INF/xfire/services.xml";

    private final static String ENCODING_STYLE_URI = "http://schemas.xmlsoap.org/soap/encoding/";

    private final static String SOAP_12 = "1.2";

    private Log log = LogFactory.getLog(XFireConfigurableServlet.class);

    /**
     * @see javax.servlet.Servlet#init()
     */
    public void init()
        throws ServletException
    {
        super.init();
        try
        {
            configureXFire();
        }
        catch (Exception e)
        {
            log.error("Couldn't configure XFire", e);
        }
    }

    protected void configureXFire()
        throws Exception
    {
        XMLServiceBuilder builder = new XMLServiceBuilder(getXFire());
        log.info("Searching for META-INF/xfire/services.xml");
        
        // get services.xml
        Enumeration en = getClass().getClassLoader().getResources(CONFIG_FILE);
        while (en.hasMoreElements())
        {
            URL resource = (URL) en.nextElement();
            
            builder.buildServices( resource.openStream() );
        }
    }

}
