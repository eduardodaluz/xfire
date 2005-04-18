package org.codehaus.xfire.annotations.backport175;

/**
 * backport 175 version of the HandlerChain annotation.
 *
 * @author Arjen Poutsma
 * @see org.codehaus.xfire.annotations.HandlerChainAnnotation
 */
public interface HandlerChain
{
    /**
     * Returns the location of the handler chain file. The location is a URL, which may be relative or absolute.
     * Relative URLs are relative to the location of the service implementation bean at the time of processing.
     *
     * @return the location of the handler chain file.
     */
    String file();

    /**
     * Returns the name of the handler chain in the configuration file.
     *
     * @return the name of the handler chain.
     */
    String name();

}
