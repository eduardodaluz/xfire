package org.codehaus.xfire.security.wssecurity;

import org.codehaus.xfire.security.handlers.OutSecurityHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JOutSecurityHandler
    extends OutSecurityHandler
{

    public WSS4JOutSecurityHandler(){
        setProcessor(new WSS4JOutSecurityProcessor());
    }
    
}
