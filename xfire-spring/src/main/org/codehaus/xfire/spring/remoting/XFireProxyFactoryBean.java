package org.codehaus.xfire.spring.remoting;

import java.net.MalformedURLException;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for XFire proxies. Behaves like the proxied service when used as bean reference, exposing the specified
 * service interface.
 * <p/>
 * The service URL must be an HTTP URL exposing a SOAP service. For details, see <code>XFireClientInterceptor</code>
 * docs.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see #setServiceInterface
 * @see #setServiceUrl
 */
public class XFireProxyFactoryBean
        extends XFireClientInterceptor
        implements FactoryBean
{
    private Object serviceProxy;

    public void afterPropertiesSet()
            throws MalformedURLException
    {
        super.afterPropertiesSet();

        this.serviceProxy = ProxyFactory.getProxy(getService().getServiceClass(), this);
    }

    public Object getObject()
    {
        return this.serviceProxy;
    }

    public Class getObjectType()
    {
        return (this.serviceProxy != null) ? this.serviceProxy.getClass() : getService().getServiceClass();
    }

    public boolean isSingleton()
    {
        return true;
    }

}
