package org.codehaus.xfire.xmlbeans.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.apache.velocity.VelocityContext;
import org.codehaus.xfire.xmlbeans.generator.WSDLInspector.Service;

/**
 * Generic strategy for creating a client stub.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 2, 2004
 */
public class ServerGenerationStrategy 
    extends VelocityGenerationStrategy
    implements GenerationStrategy
{
    public void write( Service service, File outputDir, GeneratorTask task ) 
        throws Exception
    {
        File dir = new File(outputDir + File.separator + task.getPackage().replace('.','/'));
        
        if ( !dir.exists() )
            dir.mkdirs();

        File stub = new File(dir, service.getName() + ".java" );
        
        if ( !stub.exists() || task.isOverwrite() )
        {
            FileWriter writer = new FileWriter(stub);
            
            VelocityContext context = new VelocityContext();
            context.put("package", task.getPackage());
            context.put("service", service);
            
            generateStub(context, writer, new InputStreamReader(getClass().getResourceAsStream("ServerIntfStub.vm")));
            writer.close();
        }
    }

}
