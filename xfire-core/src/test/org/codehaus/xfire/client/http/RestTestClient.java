package org.codehaus.xfire.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.xfire.client.ClientHandler;
import org.codehaus.xfire.fault.XFireFault;

/**
 * Fakes a real service and returns echo.xml
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class RestTestClient
    extends RestHttpClient
{
    public RestTestClient(ClientHandler handler)
    {
        super(handler, null);
    }

    public void invoke() throws IOException, XFireFault
    {
        writeRequest(new ByteArrayOutputStream());
        
        InputStream is = getClass().getResourceAsStream("/org/codehaus/xfire/client/http/echo.xml");
        readResponse(new InputStreamReader(is));
    } 
}
