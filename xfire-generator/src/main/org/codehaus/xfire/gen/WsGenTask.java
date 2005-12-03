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

    public void execute()
        throws BuildException
    {
        super.execute();
        
        Wsdl11Generator generator = new Wsdl11Generator();
        generator.setDestinationPackage(_package);
        generator.setOutputDirectory(outputDirectory);
        generator.setWsdl(wsdl);
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
}