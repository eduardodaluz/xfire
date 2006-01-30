package org.codehaus.xfire.security;

import java.util.Map;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface SecurityProcessorBuilder
{

    void build(SecurityProcessor processor);

    public Map getConfiguration();

    public void setConfiguration(Map configuration);

}
