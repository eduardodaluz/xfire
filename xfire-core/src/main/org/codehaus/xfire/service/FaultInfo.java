package org.codehaus.xfire.service;

import java.util.Iterator;

/**
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class FaultInfo
        extends MessageInfo
{
    /**
     * Initializes a new instance of the <code>FaultInfo</code> class with the given name.
     *
     * @param name the name.
     */
    public FaultInfo(String name)
    {
        super(name);
    }

    /**
     * Acceps the given visitor. Iterates over all message part infos.
     *
     * @param visitor the visitor.
     */
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
        for (Iterator iterator = getMessageParts().iterator(); iterator.hasNext();)
        {
            MessagePartInfo messagePartInfo = (MessagePartInfo) iterator.next();
            messagePartInfo.accept(visitor);
        }
    }
}
