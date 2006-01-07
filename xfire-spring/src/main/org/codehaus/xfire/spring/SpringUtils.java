package org.codehaus.xfire.spring;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class SpringUtils
{
    /**
     * Gets the actual bean underlying the AOP proxies.
     * 
     * @param bean
     * @return
     * @throws Exception
     */
    public static Object getUserTarget(Object bean)
        throws Exception
    {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised)
        {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
            return getUserTarget(bean);
        }
        return bean;
    }
}
