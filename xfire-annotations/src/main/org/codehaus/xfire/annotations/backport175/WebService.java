/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.xfire.annotations.backport175;

/**
 * backport175 version of the WebService Annotation.
 *
 * @author Arjen Poutsma
 * @see org.codehaus.xfire.annotations.WebServiceAnnotation
 */
public interface WebService
{
    /**
     * Returns the complete name of the service endpoint interface defining the service’s abstract Web Service
     * contract.
     *
     * @return the name of the service endpoint interface.
     */
    String endpointInterface();

    /**
     * Returns the name of the Web Service. Used as the name of the wsdl:portType when mapped to WSDL 1.1.  Defaults to
     * the simple name of the Java class or interface.
     *
     * @return the name of the Web Service.
     */
    String name();

    /**
     * Returns the service name of the Web Service. Used as the name of the wsdl:service when mapped to WSDL 1.1.  Not
     * allowed on interfaces. Defaults to the simple name of the Java class + “Service".
     *
     * @return the service name of the Web Service.
     */
    String serviceName();

    /**
     * Returns the XML namespace used for the WSDL and XML elements generated from this Web Service.
     *
     * @return the XML namespace used.
     */
    String targetNamespace();
}
