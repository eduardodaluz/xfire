package org.codehaus.xfire.transport.http;

import javax.xml.stream.XMLStreamReader;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.EchoHandler;

/**
 * Stuffs something in the session.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class MockSessionHandler
    extends EchoHandler
{
    public static boolean inSession = false;

    public void invoke( MessageContext context, XMLStreamReader reader ) 
        throws XFireFault
    {
        super.invoke( context, reader );
        
        System.out.println("putting in session");
        context.getSession().put("key", "hello world");
        
        if ( context.getSession().get("key").equals("hello world") )
            inSession = true;
	}
	
}
