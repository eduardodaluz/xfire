package org.codehaus.xfire;

import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * <p>Central processing point for XFire. This can be instantiated
 * programmatically by using one of the implementations (such as
 * <code>DefaultXFire</code> or can be managed by a container like
 * Pico or Plexus.
 * </p>
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface XFire
{
	final public static String ROLE = XFire.class.getName();

	/**
	 * Processes a new SOAP Message request.  If the request is not a SOAP
     * Message an appropriate Fault is thrown.
	 */
    void invoke( InputStream in,
                 MessageContext context );

    /**
     * Generate WSDL for a service.
     * 
     * @param service
     */
    void generateWSDL(String service, OutputStream out);

    ServiceRegistry getServiceRegistry();
    
    TransportManager getTransportManager();
}