package org.codehaus.xfire.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.xfire.client.ClientHandler;

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

    public void invoke() throws IOException
    {
        writeRequest(new ByteArrayOutputStream());
        
        InputStream is = getClass().getResourceAsStream("/org/codehaus/xfire/client/http/echo.xml");
        readResponse(is);
    } 
}
