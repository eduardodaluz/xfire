package org.codehaus.xfire.gen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.spring.XFireConfigLoader;

/**
 * A Java 2 WSDL generation Ant task for XFire. Allows to generate the Wsdl without 
 * firing up the web server.
 * 
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 */
public class WsdlGenTask extends Task
{
    private static final Log LOGGER = LogFactory.getLog( WsdlGenTask.class );

    private String configUrl;

    private String outputDirectory;

    private File generatedFile;

    private ClassLoader overridingContextClassLoader = XFireConfigLoader.class.getClassLoader();

    /**
     * The mojo will set the classloader.
     * You may want to override the class loader with one that knows about your services
     * @param overridingContextClassLoader
     */
    public void setOverridingContextClassLoader(ClassLoader overridingContextClassLoader)
    {
        this.overridingContextClassLoader = overridingContextClassLoader;
    }

    public void setConfigUrl(String configUrl)
    {
        this.configUrl = configUrl;
    }

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
     * @return the File when succesfully generated
     */
    public File getGeneratedFile()
    {
        return generatedFile;
    }

    public void execute() throws BuildException
    {
        // Ugly fix for XFIRE-245 & similar: wsgen can't find XMLInputFactory
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        // displayClasspath(originalCL, "originalCL");

        Thread.currentThread().setContextClassLoader(overridingContextClassLoader);
        // displayClasspath(overridingContextClassLoader, "classLoader");

        XFireConfigLoader configLoader = new XFireConfigLoader();

        XFire xfire;

        try {
            xfire = configLoader.loadConfig( configUrl );
        } catch (XFireException e) {
            throw new BuildException( "Failure to load the configUrl", e);
        }

        final ServiceRegistry serviceRegistry = xfire.getServiceRegistry();

        Collection services = serviceRegistry.getServices();

        File outputDir = new File( outputDirectory );

        if ( ! outputDir.exists() && ! outputDir.mkdirs() )
        {
           getLogger().warn( "the output directory " + outputDirectory
                    + " doesn't exist and couldn't be created. The task with probably fail." );
        }

        OutputStream stream;

        for (Iterator iterator = services.iterator(); iterator.hasNext();)
        {

            Service service = (Service) iterator.next();

            String serviceName = service.getName().getLocalPart();

            File outputFile = new File(outputDir, serviceName + ".wsdl");

            FileOutputStream out;
            try {
                out = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                throw new BuildException("Unable to generate WSDL: output file " + outputFile + " not found", e);
            }

            stream = new BufferedOutputStream( out);

            try
            {
                service.getWSDLWriter().write( stream );
            } catch (IOException e) {
                throw new BuildException("Unable to generate WSDL.", e);
            }
            generatedFile = outputFile;
        }

        Thread.currentThread().setContextClassLoader(originalCL);
    }
}
