package org.codehaus.xfire.handler;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;

public class TransformHandler
    extends AbstractHandler
{

    public void invoke(MessageContext context)
        throws Exception
    {
        XMLStreamWriter writer = getSource();
        
        // write 
    }

    public XMLStreamWriter getSource()
    {
        return null;
    }
}
