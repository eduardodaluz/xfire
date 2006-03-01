package org.codehaus.xfire.security;

import org.codehaus.xfire.security.exceptions.WSSecurityException;
import org.w3c.dom.Document;

public interface SecurityProcessor
{

    SecurityResult process(Document document)
        throws WSSecurityException;

}
