package org.codehaus.xfire.loom;

/**
 * Component to deploy components in XFire using (optional) predefined configurations
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public interface ServiceDeployer
{
    /**
     * Deploy the specified object via XFire. The key points to a predefined configuration that this component may have.
     * If the key is not recognized by this component, a template configuration will be used.
     *
     * @param key    Key of service to deploy. Required.
     * @param object Object to deploy as service. Required.
     */
    void deploy( String key, Object object ) throws Exception;

    /**
     * Undeploy the specified object as previously deployed with {@link #deploy(String, Object)}
     *
     * @param key Key of service to undeploy. Required.
     */
    void undeploy( String key );
}