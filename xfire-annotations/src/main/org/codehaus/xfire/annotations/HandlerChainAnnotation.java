package org.codehaus.xfire.annotations;

/**
 * Represents a common representation of a handler chain annotation. Associates the Web Service with an externally
 * defined handler chain. This annotation is typically used in scenarios where embedding the handler configuration
 * directly in the Java source is not appropriate; for example, where the handler configuration needs to be shared
 * across multiple Web Services, or where the handler chain consists of handlers for multiple transports.
 * <p/>
 * It is an error to combine this annotation with the {@link org.codehaus.xfire.annotations.soap.SOAPMessageHandlerAnnotation}.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class HandlerChainAnnotation
{
    private String file;
    private String name;

    /**
     * Returns the location of the handler chain file. The location is a URL, which may be relative or absolute.
     * Relative URLs are relative to the location of the service implementation bean at the time of processing.
     *
     * @return the location of the handler chain file.
     */
    public String getFile()
    {
        return file;
    }

    /**
     * Sets the location of the handler chain file. The location is a URL, which may be relative or absolute. Relative
     * URLs are relative to the location of the service implementation bean at the time of processing.
     *
     * @param file the new location of the handler chain file.
     */
    public void setFile(String file)
    {
        this.file = file;
    }

    /**
     * Returns the name of the handler chain in the configuration file.
     *
     * @return the name of the handler chain.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the handler chain in the configuration file.
     *
     * @param name the name of the handler chain.
     */
    public void setName(String name)
    {
        this.name = name;
    }

}
