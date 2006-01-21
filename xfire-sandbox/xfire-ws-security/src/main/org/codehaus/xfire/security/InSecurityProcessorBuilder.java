package org.codehaus.xfire.security;

import java.util.Map;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public interface InSecurityProcessorBuilder
{

    void build(InSecurityProcessor processor);

    Map getConfiguration();

    void setConfiguration(Map properties);

}
