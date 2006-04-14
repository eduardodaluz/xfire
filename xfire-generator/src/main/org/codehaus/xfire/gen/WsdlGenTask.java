package org.codehaus.xfire.gen;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.AntClassLoader;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireException;
import org.codehaus.xfire.spring.XFireConfigLoader;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.Service;

import java.util.Collection;
import java.util.Iterator;
import java.io.*;
import java.net.URLClassLoader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static void displayClasspath(ClassLoader classLoader, String message)
    {
        getLogger().info("------ " + message + ":" +  classLoader);
        if (classLoader == null)
        {
            return;
        }
        if ( classLoader instanceof URLClassLoader )
        {
            URLClassLoader cl = (URLClassLoader) classLoader;
            URL[] urls = cl.getURLs();
            for (int i = 0; i < urls.length; i++) {
                URL urL = urls[i];
                getLogger().info("URL " + i + ":" +  urL);
            }
        }
        else if ( classLoader instanceof AntClassLoader)
        {
            AntClassLoader cl = (AntClassLoader) XFireConfigLoader.class.getClassLoader();
            String[] urls = cl.getClasspath().split(File.pathSeparator);
            for (int i = 0; i < urls.length; i++)
            {
                String url = urls[i];
                getLogger().info("URL " + i + ":" +  url);
            }
        } else
        {
            // not handled
        }
        displayClasspath(classLoader.getParent(), "parent->" + message);
    }
}
