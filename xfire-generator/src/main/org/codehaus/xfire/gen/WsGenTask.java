package org.codehaus.xfire.gen;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.codehaus.xfire.gen.jsr181.Jsr181Profile;

public class WsGenTask extends MatchingTask
{
    private String wsdl;
    private String outputDirectory;
    private String _package;
    private String profile = Jsr181Profile.class.getName();
    private String binding;
    private String baseURI;
    private String externalBindings;
    private boolean explicitAnnotation;
    private boolean overwrite;
    private boolean generateServerStubs = true;
    
    public void execute()
        throws BuildException
    {
        super.execute();
        
        // Ugly fix for XFIRE-245: wsgen can't find XMLInputFactory
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
    	Thread.currentThread().setContextClassLoader(Wsdl11Generator.class.getClassLoader());

        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setDestinationPackage(_package);
        generator.setOutputDirectory(outputDirectory);
        generator.setWsdl(wsdl);
        generator.setBaseURI(baseURI);
        generator.setExternalBindings(externalBindings);
        generator.setExplicitAnnotation(explicitAnnotation);
        generator.setOverwrite(overwrite);
        generator.setGenerateServerStubs(generateServerStubs);
        
        if (binding != null) generator.setBinding(binding);
        if (profile != null) generator.setProfile(profile);
        
        try
        {
            generator.generate();
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
        
    	Thread.currentThread().setContextClassLoader(originalCL);
    }

    public String getBaseURI()
    {
        return baseURI;
    }

    public void setBaseURI(String baseURI)
    {
        this.baseURI = baseURI;
    }

    public String getPackage()
    {
        return _package;
    }

    public void setPackage(String _package)
    {
        this._package = _package;
    }

    public String getBinding()
    {
        return binding;
    }

    public void setBinding(String binding)
    {
        this.binding = binding;
    }

    public String getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }

    public String getWsdl()
    {
        return wsdl;
    }

    public void setWsdl(String wsdl)
    {
        this.wsdl = wsdl;
    }

    public String getExternalBindings()
    {
        return externalBindings;
    }

    public void setExternalBindings(String externalBindings)
    {
        this.externalBindings = externalBindings;
    }

	public boolean isExplicitAnnotation() {
		return explicitAnnotation;
	}

	public void setExplicitAnnotation(boolean explicitAnnotation) {
		this.explicitAnnotation = explicitAnnotation;
	}

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean forceOverwrite)
    {
        this.overwrite = forceOverwrite;
    }

    public boolean isGenerateServerStubs()
    {
        return generateServerStubs;
    }

    public void setGenerateServerStubs(boolean generateServerStubs)
    {
        this.generateServerStubs = generateServerStubs;
    }
    
    
}