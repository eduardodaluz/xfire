package org.codehaus.xfire.xmlbeans;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;

/**
 * A handler which captures a request and echos a response.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TestHandler
	extends AbstractXMLBeansHandler
{
    private XmlObject[] request;
    
    public XmlObject[] invoke(XmlObject[] request, MessageContext context) throws Exception
    {
        this.request = request;
        // we can't echo the request because of a bug in xmlbeans.
        return null;
    }

    public XmlObject[] getRequest()
    {
        return request;
    }
}
