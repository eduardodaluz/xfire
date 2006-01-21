package org.codehaus.xfire.security;

import java.util.Map;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface OutSecurityProcessorBuilder
{

    void build(OutSecurityProcessor processor);

    public Map getConfiguration();

    public void setConfiguration(Map configuration);

}
