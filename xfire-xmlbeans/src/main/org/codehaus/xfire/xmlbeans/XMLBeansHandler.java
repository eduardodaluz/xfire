package org.codehaus.xfire.xmlbeans;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;

/**
 * An XMLBeans handler which can be used in SOAP and REST services.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Oct 31, 2004
 */
public interface XMLBeansHandler
{
    void handleObject( XmlObject body, MessageContext context ) throws XFireFault;
    
    XmlObject getResponseObject( MessageContext context );
}
