package org.codehaus.xfire.xmlbeans.generator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * An ant task which takes a WSDL and generates the SOAP 
 * client stubs.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 25, 2004
 */
public class GeneratorTask
    extends Task
{
    private String wsdl;
    private boolean overwrite = false;
    private File outputDir;
    private String packageName = "";
    private String strategy = ClientGenerationStrategy.class.getName();
    private String name;
    
    public GeneratorTask()
    {
    }

    public void execute() throws BuildException
    {
        try
        {
            WSDLInspector insp = new WSDLInspector();

            URL url;
            try
            {
                url = new URL(wsdl);
            }
            catch (MalformedURLException e )
            {
                url = new File(wsdl).toURL();
            }
            
            List services = insp.generateServices( url );
            
            GenerationStrategy strat = getStrategy();
            
            for ( Iterator itr = services.iterator(); itr.hasNext(); )
            {
                strat.write( (WSDLInspector.Service) itr.next(), outputDir, this );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

    public GenerationStrategy getStrategy() 
        throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return (GenerationStrategy) getClass().getClassLoader().loadClass(strategy).newInstance(); 
    }
    
    public String getWsdl()
    {
        return wsdl;
    }
    
    public void setWsdl(String wsdl)
    {
        this.wsdl = wsdl;
    }
    
    public File getOutputDir()
    {
        return outputDir;
    }
    
    public void setOutputDir(File outputDir)
    {
        this.outputDir = outputDir;
    }
    
    public boolean isOverwrite()
    {
        return overwrite;
    }
    
    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }
    
    public String getPackage()
    {
        return packageName;
    }
    
    public void setPackage(String packageName)
    {
        this.packageName = packageName;
    }
    
    public void setStrategy(String strategy)
    {
        this.strategy = strategy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
