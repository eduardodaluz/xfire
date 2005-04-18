package org.codehaus.xfire.annotations.commons;

import org.codehaus.xfire.annotations.HandlerChainAnnotation;

/**
 * Commons Attributes version of the HandlerChain Annotation.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @@org.apache.commons.attributes.Target(org.apache.commons.attributes.Target.CLASS)
 * @see HandlerChainAnnotation
 */
public class HandlerChain
        extends HandlerChainAnnotation
{
    /**
     * Initializes a new instance of the <code>HandlerChain</code> attribute with the given file and chain name.
     *
     * @param file the file name.
     * @param name the handler name.
     */
    public HandlerChain(String file, String name)
    {
        super(file, name);
    }

}
