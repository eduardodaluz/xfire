package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.wsdl.WSDLException;

/**
 * Create a WSDL instance from a URI.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ResourceWSDL
	implements WSDL
{
    String wsdlUrl = null;
    
    /**
     * @param wsdlUri
     */
    public ResourceWSDL(String wsdlUrl) throws WSDLException
    {
        this.wsdlUrl = wsdlUrl;
    }
    
    public void write(OutputStream out) throws IOException
    {
       URL url = new URL(wsdlUrl);
       
       copy( url.openStream(), out, 8096 );
    }
    
    public void copy( final InputStream input,
                             final OutputStream output, 
                             final int bufferSize )
            throws IOException
    {
        final byte[] buffer = new byte[bufferSize];
        
        int n = 0;
        while (-1 != (n = input.read( buffer )))
        {
            output.write( buffer, 0, n );
        }
    }
}
