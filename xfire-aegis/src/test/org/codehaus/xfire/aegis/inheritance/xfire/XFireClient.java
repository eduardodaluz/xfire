package org.codehaus.xfire.aegis.inheritance.xfire;

import org.codehaus.xfire.aegis.inheritance.ws1.WS1;
import org.codehaus.xfire.aegis.inheritance.ws1.WS1Exception;
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
public class XFireClient
{
    public static void main(String[] args)
        throws Exception
    {
        if (args.length != 1)
        {
            System.err.println("Usage : XFireClient <url>");
            System.exit(1);
        }

        XFireHelper xFireHelper = new XFireHelper();

        // test WS1
        WS1 ws1Proxy = (WS1) xFireHelper.createClientProxy(xFireHelper.createServiceWS1(), args[0]);

        System.out.println(ws1Proxy.getBeanA());
        System.out.println(ws1Proxy.getBeanB());

        System.out.println(ws1Proxy.getBean("a"));
        System.out.println(ws1Proxy.getBean("b"));
        System.out.println(ws1Proxy.getBean("c"));

        System.out.println(ws1Proxy.getRootBean("a"));
        System.out.println(ws1Proxy.getRootBean("b"));
        System.out.println(ws1Proxy.getRootBean("c"));

        try
        {
            ws1Proxy.throwException(false);
        }
        catch (WS1Exception e)
        {
            System.out.println(e);
        }
        catch (Throwable t)
        {
            System.out.println();
            t.printStackTrace(System.out);
            System.out.println();
        }

        try
        {
            ws1Proxy.throwException(true);
        }
        catch (WS1Exception e)
        {
            System.out.println(e);
        }
        catch (Throwable t)
        {
            System.out.println();
            t.printStackTrace(System.out);
            System.out.println();
        }

        // test WS2
        WS2 ws2Proxy = (WS2) xFireHelper.createClientProxy(xFireHelper.createServiceWS2(), args[0]);

        System.out.println(ws2Proxy.getParentBean("X"));
        System.out.println(ws2Proxy.getParentBean("Y"));

        String baseId = System.currentTimeMillis() + "-";
        ParentBean parentBean;

        parentBean = new ParentBean(baseId + "A", new ContentBean1("data1-A"));
        ws2Proxy.putParentBean(parentBean);
        System.out.println(ws2Proxy.getParentBean(baseId + "A"));

        parentBean = new ParentBean(baseId + "B", new ContentBean2("data1-B", "content2-B"));
        ws2Proxy.putParentBean(parentBean);
        System.out.println(ws2Proxy.getParentBean(baseId + "B"));

        try
        {
            ws2Proxy.putParentBean(parentBean);
        }
        catch (AlreadyExistsException e)
        {
            System.out.println(e);
        }
        catch (Throwable t)
        {
            System.out.println();
            t.printStackTrace(System.out);
            System.out.println();
        }

        try
        {
            ws2Proxy.getParentBean("Z");
        }
        catch (NotFoundException e)
        {
            System.out.println(e);
        }
        catch (Throwable t)
        {
            System.out.println();
            t.printStackTrace(System.out);
            System.out.println();
        }
    }
}
