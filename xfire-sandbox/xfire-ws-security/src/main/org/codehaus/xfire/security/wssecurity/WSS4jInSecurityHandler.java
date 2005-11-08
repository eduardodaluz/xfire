package org.codehaus.xfire.security.wssecurity;

import org.codehaus.xfire.security.handlers.InSecurityHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4jInSecurityHandler
    extends InSecurityHandler
{

    public WSS4jInSecurityHandler(){
        setProcessor(new WSS4JInSecurityProcessor());
    }
  
}
