package org.codehaus.xfire.wsdl;

import java.io.IOException;
import java.io.OutputStream;
import javax.wsdl.WSDLException;

/**
 * Create a WSDL instance from a URI.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ResourceWSDL
	implements WSDL
{
    String uri = null;
    
    /**
     * @param wsdlUri
     */
    public ResourceWSDL(String wsdlUri) throws WSDLException
    {
        this.uri = wsdlUri;
    }
    
    public void write(OutputStream out) throws IOException
    {
       
    }
}
