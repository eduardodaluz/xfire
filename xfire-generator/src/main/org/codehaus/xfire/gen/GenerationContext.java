package org.codehaus.xfire.gen;

import java.io.File;

import org.codehaus.xfire.AbstractContext;
import org.codehaus.xfire.service.Service;

import com.sun.codemodel.JCodeModel;

public class GenerationContext
    extends AbstractContext
{
    private JCodeModel codeModel;
    private Service service;
    private Object wsdl;
    private String destinationPackage;
    private SchemaSupport schemaGenerator;
    private File outputDirectory;
    
    public GenerationContext(JCodeModel model, Object wsdl)
    {
        codeModel = model;
        this.wsdl = wsdl;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public JCodeModel getCodeModel()
    {
        return codeModel;
    }

    public void setService(Service service)
    {
        this.service = service;
    }

    public Service getService()
    {
        return service;
    }

    public Object getWsdl()
    {
        return wsdl;
    }
    
    public File getBaseDir()
    {
        return new File(".");
    }

    public String getDestinationPackage()
    {
        return destinationPackage;
    }

    public void setDestinationPackage(String destinationPackage)
    {
        this.destinationPackage = destinationPackage;
    }

    public SchemaSupport getSchemaGenerator()
    {
        return schemaGenerator;
    }

    public void setSchemaGenerator(SchemaSupport schemaGenerator)
    {
        this.schemaGenerator = schemaGenerator;
    }
    
}
