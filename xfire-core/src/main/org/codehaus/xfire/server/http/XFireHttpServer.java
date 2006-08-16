package org.codehaus.xfire.server.http;

import java.io.File;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.server.XFireServer;
import org.codehaus.xfire.transport.http.XFireServlet;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SslListener;
import org.mortbay.jetty.Server;
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
    private Server httpServer;

    // properties
    private int port = 8081;

    private XFire xfire;

    public XFireHttpServer() {}
    
    private File keystore;
    private String keystorePassword;
    private String keyPassword;
    
    public XFireHttpServer(File keystore, String keystorePassword, String keyPassword) {
        this(XFireFactory.newInstance().getXFire(), keystore, keystorePassword, keyPassword);
    }
    
    public XFireHttpServer(XFire xfire, File keystore, String keystorePassword, String keyPassword) {
        this.xfire = xfire;
        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.keyPassword = keyPassword;
    }
    
    public XFireHttpServer(XFire xfire) 
    {
        this.xfire = xfire;
    }
    
    public void start()
        throws Exception
    {
        if (isStarted()) {
            return;
        }
        
        httpServer = new Server();
        if (keystore != null)
        {
            SslListener listener = new SslListener(new InetAddrPort(port));
            listener.setKeystore(keystore.getAbsolutePath());
            listener.setKeyPassword(keystorePassword);
            listener.setPassword(keyPassword);
            httpServer.addListener(listener);
        }
        else
        {
            httpServer.addListener(new InetAddrPort(port));
        }
        
        HttpContext context = httpServer.getContext("/");
        context.setRequestLog(null);
        
        ServletHandler handler = new ServletHandler();
        handler.addServlet("XFireServlet", "/*", XFireServlet.class.getName());
        
        if (xfire != null)
            handler.getServletContext().setAttribute(XFireServlet.XFIRE_INSTANCE, xfire);
            
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
