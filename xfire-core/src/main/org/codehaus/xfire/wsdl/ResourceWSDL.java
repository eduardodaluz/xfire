package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.codehaus.xfire.util.Resolver;

/**
 * Create a WSDL instance from a URI.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ResourceWSDL
	implements WSDLWriter
{
    private final URL wsdlUrl;

    /**
     * @param wsdlUrl
     */
    public ResourceWSDL(String wsdlUrl) throws IOException
    {
        this.wsdlUrl = new Resolver(wsdlUrl).getURL();
    }

    public ResourceWSDL(String baseUri, String wsdlUrl) throws IOException
    {
        this.wsdlUrl = new Resolver(baseUri, wsdlUrl).getURL();
    }
    
    /**
     * @param wsdlUrl
     */
    public ResourceWSDL(URL wsdlUrl)
    {
        this.wsdlUrl = wsdlUrl;
    }

    public void write(OutputStream out) throws IOException
    {
       copy( wsdlUrl.openStream(), out, 8096 );
    }

    private void copy(final InputStream input,
                     final OutputStream output,
                     final int bufferSize)
        throws IOException
    {
        try
        {
            final byte[] buffer = new byte[bufferSize];

            int n = 0;
            while (-1 != (n = input.read(buffer)))
            {
                output.write(buffer, 0, n);
            }
        }
        finally
        {
            input.close();
        }
    }
}
