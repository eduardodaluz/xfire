package org.codehaus.xfire.jaxws;

import org.codehaus.xfire.server.http.XFireHttpServer;

/**
 * Starts and Stops the XFireHttpServer.
 * 
 * @author Dan Diephouse
 */
public class AbstractJAXWSHttpTest
    extends AbstractJAXWSTest
{
    private XFireHttpServer server;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        server = new XFireHttpServer();
        server.setPort(8191);
        server.start();
    }
    
    @Override
    protected void tearDown()
        throws Exception
    {
        server.stop();
        
        super.tearDown();
    }
}
