package org.codehaus.xfire.xmlbeans.generator;

import java.io.File;

/**
 * A strategy for generating stubs.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 2, 2004
 */
public interface GenerationStrategy
{
    
    /**
     * Write out a stub for the service into the specified directory.
     * 
     * @param service
     * @param directory
     */
    void write( WSDLInspector.Service service, 
                File directory,
                GeneratorTask task )
        throws Exception; 
}
