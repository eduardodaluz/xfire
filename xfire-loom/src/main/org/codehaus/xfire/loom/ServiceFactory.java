package org.codehaus.xfire.loom;

import org.apache.avalon.framework.configuration.Configuration;

import org.codehaus.xfire.service.Service;

/**
 * Interface for components that are capable of creating services given an object and a configuration
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public interface ServiceFactory
{
    /**
     * Get the type of services that this factory can create (eg, simple, java, xmlbean, etc)
     *
     * @return Type of services that this factory can create. Required.
     */
    String getType();

    /**
     * Create an XFire service for the specified Loom service with the specified configuration.
     *
     * @param target        Object to be target of service
     * @param configuration Configuration for service
     *
     * @return {@link Service} instance
     *
     * @throws Exception on failure to create service
     */
    Service createService( Object target, Configuration configuration ) throws Exception;
}