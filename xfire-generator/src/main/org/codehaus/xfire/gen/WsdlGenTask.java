package org.codehaus.xfire.gen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.codehaus.xfire.service.Service;

/**
 * A Java 2 WSDL generation Ant task for XFire. Allows to generate the Wsdl
 * without firing up the web server.
 * 
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 */
public class WsdlGenTask
    extends BaseServicesGenTask
{
    private static final Log LOGGER = LogFactory.getLog(WsdlGenTask.class);


    private String outputDirectory;

    private File generatedFile;

    private File outputDir;

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    private static Log getLogger()
    {
        return LOGGER;
    }

    /**
     * Path of the generatedFile
     * 
     * @return the File when succesfully generated
     */
    public File getGeneratedFile()
    {
        return generatedFile;
    }

    public void execute()
        throws BuildException
    {

        outputDir = new File(outputDirectory);

        if (!outputDir.exists() && !outputDir.mkdirs())
        {
            getLogger().warn("the output directory " + outputDirectory
                    + " doesn't exist and couldn't be created. The task with probably fail.");
        }

        iterateServices();
    }


    @Override
    protected void processService(Service service)
    {

        String serviceName = service.getName().getLocalPart();

        File outputFile = new File(outputDir, serviceName + ".wsdl");

        FileOutputStream out;
        try
        {
            out = new FileOutputStream(outputFile);
        }
        catch (FileNotFoundException e)
        {
            throw new BuildException("Unable to generate WSDL: output file " + outputFile
                    + " not found", e);
        }

        OutputStream stream;
        stream = new BufferedOutputStream(out);

        try
        {
            service.getWSDLWriter().write(stream);
        }
        catch (IOException e)
        {
            throw new BuildException("Unable to generate WSDL.", e);
        }
        generatedFile = outputFile;
    }

}