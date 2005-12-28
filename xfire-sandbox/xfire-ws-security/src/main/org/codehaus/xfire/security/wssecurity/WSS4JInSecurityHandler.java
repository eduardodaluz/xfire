package org.codehaus.xfire.security.wssecurity;

import org.codehaus.xfire.security.handlers.InSecurityHandler;
import org.codehaus.xfire.soap.handler.ReadHeadersHandler;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class WSS4JInSecurityHandler
    extends InSecurityHandler
{
    
    public WSS4JInSecurityHandler(){
        super();
        setProcessor(new WSS4JInSecurityProcessor());
        before(ReadHeadersHandler.class.getName());        
    }
  
}
