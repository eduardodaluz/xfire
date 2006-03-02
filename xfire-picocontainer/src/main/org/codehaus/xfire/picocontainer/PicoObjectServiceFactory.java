package org.codehaus.xfire.picocontainer;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.xfire.picocontainer.util.PicoFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.RequestScopePolicy;
import org.codehaus.xfire.service.invoker.ScopePolicy;
import org.codehaus.xfire.transport.TransportManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * PicoContainer specialization of Java objects-specific implementation of the
 * {@link ServiceFactory} interface which uses a given picocontainer to pick
 * service objects instance when it's about to invoke them.
 * 
 * @author <a href="mailto:juze@intelli.biz">Jose Peleteiro</a>
 * @author <a href="mailto:ajoo.email@gmail.com">Ben Yu</a>
 */
public class PicoObjectServiceFactory
    extends ObjectServiceFactory
{
    private final ObjectReference picoReference;
    private final ScopePolicy scope;
    public PicoObjectServiceFactory(PicoContainer pico) {
        this(enref(pico));
    }

    public PicoObjectServiceFactory(ObjectReference picoReference) {
        this(getDefaultScope(), picoReference);
    }
    /**
     * To create a PicoObjectServiceFactory.
     * @param scope the scope policy.
     * @param picoReference the reference to pico container.
     */
    public PicoObjectServiceFactory(ScopePolicy scope,
       ObjectReference picoReference) {
        this.scope = scope;
        this.picoReference = picoReference;
    }
    public PicoObjectServiceFactory(PicoContainer pico, TransportManager transportManager,
            BindingProvider provider)
    {
       this(enref(pico), transportManager, provider);
    }
    public PicoObjectServiceFactory(ObjectReference picoReference,
            TransportManager transportManager, BindingProvider provider)
    {
        this(getDefaultScope(), picoReference,
             transportManager, provider);
    }
    /**
     * Create a PicoObjectServiceFactory object.
     * @param scope the scope policy.
     * @param picoReference the reference to pico container.
     * @param transportManager the transport manager.
     * @param provider the binding provider.
     */
    public PicoObjectServiceFactory(ScopePolicy scope,
       ObjectReference picoReference, TransportManager transportManager, 
       BindingProvider provider)
    {
        super(transportManager, provider);
        this.scope = scope;
        this.picoReference = picoReference;
    }
    public Service create(Class clazz, String name, String namespace, Map properties)
    {
        return prepare(super.create(clazz, name, namespace, properties));
    }


    public Service create(Class clazz, Map properties)
    {
        return prepare(super.create(clazz, properties));
    }

    public Service create(Class clazz, QName name, URL wsdlUrl, Map properties)
    {
        return prepare(super.create(clazz, name, wsdlUrl, properties));
    }

    public Service create(Class clazz)
    {
        return prepare(super.create(clazz));
    }

    /**
     * Prepare an ObjectService to use pico.
     */
    protected Service prepare(Service endpoint)
    {
        endpoint.setInvoker(new FactoryInvoker(
          new PicoFactory(
            picoReference, endpoint.getServiceInfo().getServiceClass()
          ), scope
        ));
        return endpoint;
    }
    private static ObjectReference enref(PicoContainer pico){
        final ObjectReference result = new SimpleReference();
        result.set(pico);
        return result;
    }
    /**
     * Get the default scope policy when no scope policy is specified.
     * In this implementation, "request" is used as default.
     */
    public static ScopePolicy getDefaultScope(){
        return RequestScopePolicy.instance();
    }
}
