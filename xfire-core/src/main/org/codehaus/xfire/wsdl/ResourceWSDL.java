package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.WSDLException;

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
    public ResourceWSDL(String wsdlUrl) throws WSDLException
    {
        try
        {
            this.wsdlUrl = new URL( wsdlUrl );
        }
        catch (MalformedURLException e)
        {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, "Invalid URL to WSDL file", e);
        }
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
