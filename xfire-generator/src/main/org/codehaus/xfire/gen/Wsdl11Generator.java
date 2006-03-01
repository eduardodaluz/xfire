package org.codehaus.xfire.gen;

import java.io.File;
import java.util.Iterator;

import org.codehaus.xfire.gen.jsr181.Jsr181Profile;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceBuilder;

import com.sun.codemodel.JCodeModel;

public class Wsdl11Generator
{
    public static final String JAXB = "jaxb";
    public static final String XMLBEANS = "xmlbeans";

    private String wsdl;
    private String baseURI;
    private String outputDirectory;
    private String destinationPackage;
    
    private JCodeModel codeModel = new JCodeModel();
    
    private String profile;
    private String binding = JAXB;
    private SchemaSupport support;
    
    public void generate() throws Exception
    {
        File dest = new File(outputDirectory);
        if (!dest.exists()) dest.mkdirs();

        if (support == null)
        {
            if (binding.equals(JAXB))
            {
                support = loadSupport("org.codehaus.xfire.gen.jaxb.JAXBSchemaSupport");
            }
            else if (binding.equals(XMLBEANS))
            {
                support = loadSupport("org.codehaus.xfire.gen.xmlbeans.XmlBeansSchemaSupport");
            }
            else
            {
                throw new Exception("Illegal binding: " + binding);
            }
        }

        WSDLServiceBuilder builder = new WSDLServiceBuilder(baseURI, new WSDLInputStreamLoader().getInputStream(wsdl));
        builder.setBindingProvider(support.getBindingProvider());
        builder.build();
        
        if (profile == null) profile = Jsr181Profile.class.getName();
        PluginProfile profileObj = 
            (PluginProfile) ClassLoaderUtils.loadClass(profile, getClass()).newInstance();
        
        GenerationContext context = new GenerationContext(codeModel, builder.getDefinition());
        context.setOutputDirectory(dest);
        context.setWsdlLocation(wsdl);
        context.setBaseURI(baseURI);
        context.setSchemas(builder.getSchemaElements());
        
        support.initialize(context);

        context.setServices(builder.getServices());
        context.setDestinationPackage(getDestinationPackage());
        context.setSchemaGenerator(support);
        
        for (Iterator<GeneratorPlugin> pitr = profileObj.getPlugins().iterator(); pitr.hasNext();)
        {
            GeneratorPlugin plugin = pitr.next();
            
            plugin.generate(context);
        }

        // Write the code!
        codeModel.build(dest);
    }
  
    private SchemaSupport loadSupport(String name) throws Exception
    {
        return (SchemaSupport) ClassLoaderUtils.loadClass(name, getClass()).newInstance();
    }

    public SchemaSupport getSchemaSupport()
    {
        return support;
    }

    public void setSchemaSupport(SchemaSupport support)
    {
        this.support = support;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public JCodeModel getCodeModel()
    {
        return codeModel;
    }

    public String getBaseURI()
    {
        return baseURI;
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }

    public String getWsdl()
    {
        return wsdl;
    }

    public void setWsdl(String wsdl)
    {
        this.wsdl = wsdl;
    }

    public String getDestinationPackage()
    {
        return destinationPackage;
    }

    public void setDestinationPackage(String destinationPackage)
    {
        this.destinationPackage = destinationPackage;
    }

    public String getBinding()
    {
        return binding;
    }

    public void setBinding(String binding)
    {
        this.binding = binding;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }
}
