package org.codehaus.xfire.jaxws.gen;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.gen.PluginProfile;
import org.codehaus.xfire.gen.jsr181.PortGenerator;
import org.codehaus.xfire.gen.jsr181.ServiceStubGenerator;

public class JAXWSProfile 
    implements PluginProfile
{
    public List<GeneratorPlugin> getPlugins()
    {
        ArrayList<GeneratorPlugin> plugins = new ArrayList<GeneratorPlugin>();
        plugins.add(new InterfaceGenerator());
        plugins.add(new ServiceStubGenerator());
        plugins.add(new ServiceGenerator());
        
        return plugins;
    }
}
