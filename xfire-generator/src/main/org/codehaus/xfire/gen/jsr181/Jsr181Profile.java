package org.codehaus.xfire.gen.jsr181;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.gen.PluginProfile;
/**
 * Generates a service interface, service stub, and port accessor.
 * 
 * @author Dan Diephouse
 */
public class Jsr181Profile
    implements PluginProfile
{
    public List<GeneratorPlugin> getPlugins()
    {
        ArrayList<GeneratorPlugin> plugins = new ArrayList<GeneratorPlugin>();
       /* plugins.add(new MappingServiceGenerator());
        plugins.add(new MappingServiceStubGenerator());
        plugins.add(new PortGenerator());*/
        plugins.add(new ServiceInterfaceGenerator());
        plugins.add(new ServiceStubGenerator());
        plugins.add(new PortGenerator());
        return plugins;
    }
}
