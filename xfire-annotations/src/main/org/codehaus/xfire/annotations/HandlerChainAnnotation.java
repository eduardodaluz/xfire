package org.codehaus.xfire.annotations;

/**
 * Represents a common representation of a handler chain annotation. Associates the Web Service with an externally
 * defined handler chain. This annotation is typically used in scenarios where embedding the handler configuration
 * directly in the Java source is not appropriate; for example, where the handler configuration needs to be shared
 * across multiple Web Services, or where the handler chain consists of handlers for multiple transports.
 * <p/>
 * It is an error to combine this annotation with the {@link org.codehaus.xfire.annotations.soap.SOAPMessageHandler}.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class HandlerChainAnnotation
{
    private String file;
    private String name;

    /**
     * Initializes a new instance of the <code>HandlerChain</code> attribute with the given file and chain name.
     *
     * @param file the file name.
     * @param name the handler name.
     */
    public HandlerChainAnnotation(String file, String name)
    {
        this.file = file;
        this.name = name;
    }

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
     * Returns the name of the handler chain in the configuration file.
     *
     * @return the name of the handler chain.
     */
    public String getName()
    {
        return name;
    }
}
