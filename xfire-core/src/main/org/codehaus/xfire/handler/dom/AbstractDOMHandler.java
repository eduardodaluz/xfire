package org.codehaus.xfire.handler.dom;

import javax.xml.namespace.QName;

/**
 * Basic helper methods for the DOMHandler.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 18, 2004
 */
public abstract class AbstractDOMHandler 
    implements DOMHandler
{
    /**
     * @see org.codehaus.xfire.handler.dom.DOMHandler#getUnderstoodHeaders()
     */
    public QName[] getUnderstoodHeaders()
    {
        return null;
    }
}
