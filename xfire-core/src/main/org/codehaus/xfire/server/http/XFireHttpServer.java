package org.codehaus.xfire.server.http;

import org.codehaus.xfire.server.XFireServer;
import org.codehaus.xfire.transport.http.XFireServlet;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;

/**
 * HTTP Server for XFire services.
 * 
 * @version $Id$
 */
public class XFireHttpServer
    implements XFireServer
{
    // components
    private HttpServer httpServer;

    // properties
    private int port = 8081;

    public void start()
        throws Exception
    {
        if (isStarted()) {
            return;
        }
        
        httpServer = new HttpServer();
        httpServer.addListener(new InetAddrPort(port));
        
        HttpContext context = httpServer.getContext("/");
        context.setRequestLog(null);
        
        ServletHandler handler = new ServletHandler();
        handler.addServlet("XFireServlet", "/*", XFireServlet.class.getName());
        
        context.addHandler(handler);
        
        httpServer.start();
    }

    public void stop()
        throws Exception
    {
        if (isStarted())
        {
            httpServer.stop();
            httpServer = null;
        }
    }

    public boolean isStarted()
    {
        return (httpServer != null) && httpServer.isStarted();
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int value)
    {
        port = value;
    }

}
