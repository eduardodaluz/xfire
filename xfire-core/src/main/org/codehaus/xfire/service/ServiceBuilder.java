package org.codehaus.xfire.service;



/**
 * Builds services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ServiceBuilder
{
    public static final String SELECTOR_ROLE = ServiceBuilder.class.getName() + "Selector";
    public static final String ROLE = ServiceBuilder.class.getName();
    
    /**
     * Build a service object and register it with the service registry.
     * 
     * @param name The name of the service.
     * @param service The reference to the service.  In most cases, this is
     * just the class name. 
     * @param style The style - wrapped/document/rpc. See SOAPConstants.
     * @param encoding The encoding - literal/encoded.  See SOAP Constants. 
     * @param version The SOAP version.  See SOAP Constants. 
     * @return
     */
    public Service build( String name, 
                          String service, 
                          String style, 
                          String encoding, 
                          String version,
                          String namespace );

	/**
     * Build a service object and register it with the service registry.
     * 
	 * @param c The plexus configuration.
	 * @return
	 * @throws Exception
	 */
	//public Service build(InputStream ) throws Exception;

    
}
