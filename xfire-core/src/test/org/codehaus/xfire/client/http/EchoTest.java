package org.codehaus.xfire.client.http;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class EchoTest
    extends TestCase
{
    public void testEcho() throws Exception
    {
        EchoClient client = new EchoClient();
        
        client.writeRequest(System.out);
        
        client.invoke();
    }
}
