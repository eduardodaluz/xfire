package org.codehaus.xfire.xmlbeans.generator;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 2, 2004
 */
public class VelocityGenerationStrategy
{
    private static VelocityEngine engine = new VelocityEngine();
    
    static
    {
        engine.addProperty("runtime.log.logsystem.log4j.category", "velocity");
        try
        {
            engine.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void generateStub(VelocityContext context, Writer writer, Reader template) 
        throws Exception
    {        
        engine.evaluate( context, writer, "", template );
        writer.flush();
    }
}
