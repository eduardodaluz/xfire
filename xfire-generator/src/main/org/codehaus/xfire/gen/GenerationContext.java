package org.codehaus.xfire.gen;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.AbstractContext;
import org.codehaus.xfire.service.Service;

import com.sun.codemodel.JCodeModel;

public class GenerationContext
    extends AbstractContext
{
    private JCodeModel codeModel;
    private Map<QName,Collection<Service>> services;
    private Object wsdl;
    private String destinationPackage;
    private String baseURI;
    private SchemaSupport schemaGenerator;
    private File outputDirectory;
    private String wsdlLocation;
    private Collection schemas;
    private Collection<File> externalBindings;
    
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

    public Map<QName,Collection<Service>> getServices()
    {
        return services;
    }

    public void setServices(Map<QName,Collection<Service>> services)
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

    public Collection<File> getExternalBindings()
    {
        return externalBindings;
    }

    public void setExternalBindings(Collection<File> externalBindings)
    {
        this.externalBindings = externalBindings;
    }
    
}
