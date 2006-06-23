package org.codehaus.xfire.aegis.inheritance.ws2;

import org.codehaus.xfire.aegis.inheritance.ws2.common.ParentBean;
import org.codehaus.xfire.aegis.inheritance.ws2.common.exception.AlreadyExistsException;
import org.codehaus.xfire.aegis.inheritance.ws2.common.exception.NotFoundException;

/**
 * <br/>
 * 
 * @author xfournet
 */
public interface WS2
{
    public void putParentBean(ParentBean parentBean)
        throws AlreadyExistsException;

    public ParentBean getParentBean(String id)
        throws NotFoundException;
}
