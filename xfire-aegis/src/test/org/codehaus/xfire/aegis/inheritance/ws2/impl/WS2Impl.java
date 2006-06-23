package org.codehaus.xfire.aegis.inheritance.ws2.impl;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.xfire.aegis.inheritance.ws2.WS2;
import org.codehaus.xfire.aegis.inheritance.ws2.common.ParentBean;
import org.codehaus.xfire.aegis.inheritance.ws2.common.exception.AlreadyExistsException;
import org.codehaus.xfire.aegis.inheritance.ws2.common.exception.NotFoundException;
import org.codehaus.xfire.aegis.inheritance.ws2.common.pack1.ContentBean1;
import org.codehaus.xfire.aegis.inheritance.ws2.common.pack2.ContentBean2;

/**
 * <br/>
 * 
 * @author xfournet
 */
public class WS2Impl
    implements WS2
{
    private Map m_map = new HashMap();

    public WS2Impl()
    {
        ParentBean x = new ParentBean("X", new ContentBean1("data1-X"));
        ParentBean y = new ParentBean("Y", new ContentBean2("data1-Y", "content2-Y"));
        m_map.put(x.getId(), x);
        m_map.put(y.getId(), y);
    }

    public synchronized void putParentBean(ParentBean parentBean)
        throws AlreadyExistsException
    {
        String id = parentBean.getId();
        if (m_map.containsKey(id))
        {
            throw new AlreadyExistsException(id);
        }
        m_map.put(id, parentBean);
    }

    public synchronized ParentBean getParentBean(String id)
        throws NotFoundException
    {
        ParentBean result = (ParentBean) m_map.get(id);
        if (result == null)
        {
            throw new NotFoundException(id);
        }

        return result;
    }
}
