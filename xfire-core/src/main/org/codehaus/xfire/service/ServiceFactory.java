package org.codehaus.xfire.service;

import java.net.URL;

import org.codehaus.xfire.soap.SoapVersion;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface ServiceFactory
{
    /**
     * Create a service from the specified class.
     * @param clazz The service class used to populate the operations and parameters.
     * @return The service.
     */
    public Service create(Class clazz);
    
    /**
     * Create a service from the specified class.
     * @param clazz The service class used to populate the operations and parameters.
     * @param version The SoapVersion.
     * @param style The service style.
     * @param use The service use. 
     * @return The service.
     * @see org.codehaus.xfire.soap.SoapConstants
     * @see org.codehaus.xfire.soap.Soap11
     * @see org.codehaus.xfire.soap.Soap12
     * @see org.codehaus.xfire.soap.SoapVersion
     */
    public Service create(Class clazz, SoapVersion version, String style, String use);
    
    /**
     * Create a service from the specified class.
     * @param clazz The service class used to populate the operations and parameters.
     * @param name The name of the service.
     * @param namespace The default namespace of the service.
     * @param version The SoapVersion.
     * @param style The service style.
     * @param use The service use.
     * @param encodingStyleURI The encoding style to use
     * @return The service.
     * @see org.codehaus.xfire.soap.SoapConstants
     * @see org.codehaus.xfire.soap.Soap11
     * @see org.codehaus.xfire.soap.Soap12
     * @see org.codehaus.xfire.soap.SoapVersion
     */
    public Service create(Class clazz,
                          String name,
                          String namespace,
                          SoapVersion version,
                          String style,
                          String use,
                          String encodingStyleURI);

    /**
     * Create a service from a WSDL file. NOTE: This probably doesn't work yet.
     * 
     * @param clazz
     *            The service class for the wsdl.
     * @param tm
     * @param wsdlUrl
     *            The WSDL URL.
     * @return
     * @throws Exception
     */
    public Service create(Class clazz, URL wsdlUrl) throws Exception;
}
