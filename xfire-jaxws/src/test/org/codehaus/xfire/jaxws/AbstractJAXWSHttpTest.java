package org.codehaus.xfire.jaxws;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.xfire.server.http.XFireHttpServer;

import com.ctc.wstx.stax.WstxInputFactory;

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
        System.setProperty(XMLInputFactory.class.getName(), WstxInputFactory.class.getName());
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
