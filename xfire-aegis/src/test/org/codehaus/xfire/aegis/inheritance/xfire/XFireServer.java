package org.codehaus.xfire.aegis.inheritance.xfire;

import org.codehaus.xfire.aegis.inheritance.ws1.impl.WS1Impl;
import org.codehaus.xfire.aegis.inheritance.ws2.impl.WS2Impl;
import org.codehaus.xfire.server.http.XFireHttpServer;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class XFireServer
{
    public static void main(String[] args)
        throws Exception
    {
        // Create WS
        XFireHelper xFireHelper = new XFireHelper();
        xFireHelper.registerService(xFireHelper.createServiceWS1(), new WS1Impl());
        xFireHelper.registerService(xFireHelper.createServiceWS2(), new WS2Impl());

        // Start Jetty server
        XFireHttpServer xFireHttpServer = new XFireHttpServer(xFireHelper.getXfire());
        xFireHttpServer.setPort(8080);
        xFireHttpServer.start();
    }
}
