package org.codehaus.xfire.client.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.xfire.client.http.SoapHttpClient;
import org.codehaus.xfire.fault.XFireFault;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 26, 2004
 */
public class EchoClient
    extends SoapHttpClient
{
    public EchoClient()
    {
        super(new EchoHandler(), "");
    }

    public void invoke() throws IOException, XFireFault
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeRequest(out);
        
        System.out.println("RESPONSE:");
        System.out.println(out.toString());
        
        readResponse(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
    }
    
    
}
