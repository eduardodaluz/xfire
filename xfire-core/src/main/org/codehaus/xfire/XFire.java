package org.codehaus.xfire;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.transport.TransportManager;

/**
 * <p>Central processing point for XFire. This can be instantiated
 * programmatically by using one of the implementations (such as
 * <code>DefaultXFire</code> or can be managed by a container like
 * Pico or Plexus.
 * </p>
 * <p>
 * Central, however, does not mean that there can be only one. 
 * Implementations can be very lightweight, creating fast generic 
 * SOAP processors.
 * </p>
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 18, 2004
 */
public interface XFire
{
	final public static String ROLE = XFire.class.getName();

	/**
	 * Processes a new SOAP Message request. Faults are handled
     * according to the message contract of the particular service.
     * A <code>XFireRuntimeException</code>s may still be thrown if
     * something fatal goes wrong in the pipeline.
     * 
     * @param in An InputStream to the SOAP document.
     * @param context The MessageContext.
	 */
    void invoke(InputStream in, MessageContext context)
        throws XFireRuntimeException;

	/**
     * Processes a new SOAP Message request. Faults are handled
     * according to the message contract of the particular service.
     * A <code>XFireRuntimeException</code>s may still be thrown if
     * something fatal goes wrong in the pipeline.
     * 
     * @param in An InputStream to the SOAP document.
     * @param context The MessageContext.
	 */
    void invoke(XMLStreamReader reader, MessageContext context)
        throws XFireRuntimeException;

    /**
     * Generate WSDL for a service.
     * 
     * @param service
     *            The name of the service.
     * @param out
     *            The OutputStream to write the WSDL to.
     */
    void generateWSDL(String service, OutputStream out);

    /**
     * Get the <code>ServiceRegistry</code>.
     */
    ServiceRegistry getServiceRegistry();
    
    /**
     * Get the <code>TransportManager</code>.
     */
    TransportManager getTransportManager();
}