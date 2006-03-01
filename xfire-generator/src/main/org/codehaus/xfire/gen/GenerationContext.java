package org.codehaus.xfire.gen;

import java.io.File;
import java.util.Collection;

import org.codehaus.xfire.AbstractContext;

import com.sun.codemodel.JCodeModel;

public class GenerationContext
    extends AbstractContext
{
    private JCodeModel codeModel;
    private Collection services;
    private Object wsdl;
    private String destinationPackage;
    private String baseURI;
    private SchemaSupport schemaGenerator;
    private File outputDirectory;
    private String wsdlLocation;
    private Collection schemas;
    
    public GenerationContext(JCodeModel model, Object wsdl)
    {
        codeModel = model;
        this.wsdl = wsdl;
    }

    public String getBaseURI()
    {
        return baseURI;
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }

    public Collection getSchemas()
    {
        return schemas;
    }

    public void setSchemas(Collection schemas)
    {
        this.schemas = schemas;
    }

    public String getWsdlLocation()
    {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation)
    {
        this.wsdlLocation = wsdlLocation;
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

    public Collection getServices()
    {
        return services;
    }

    public void setServices(Collection services)
    {
        this.services = services;
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
