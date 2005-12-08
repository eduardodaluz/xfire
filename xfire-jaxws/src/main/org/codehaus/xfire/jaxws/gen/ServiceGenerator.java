package org.codehaus.xfire.jaxws.gen;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.gen.jsr181.AbstractPlugin;
import org.codehaus.xfire.gen.jsr181.ServiceInterfaceGenerator;
import org.codehaus.xfire.gen.jsr181.ServiceStubGenerator;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.SoapBinding;
import org.codehaus.xfire.transport.local.LocalTransport;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JMods;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Generates JAX-WS Client Service artifacts.
 * @author Dan Diephouse
 */
public class ServiceGenerator
    extends AbstractPlugin
    implements GeneratorPlugin
{
    public void generate(GenerationContext context)
        throws Exception
    {
        Map<QName,Collection<Service>> q2services = 
            new HashMap<QName,Collection<Service>>();
        for (Iterator itr = context.getServices().iterator(); itr.hasNext();)
        {
            Service service = (Service) itr.next();
            if (service.getEndpoints().size() == 0) continue;
            
            Collection<Service> services = q2services.get(service.getName());
            if (services == null)
            {
                services = new ArrayList<Service>();
                q2services.put(service.getName(), services);
            }
            
            services.add(service);
        }
        
        for (Entry<QName,Collection<Service>> entry : q2services.entrySet())
        {
            generate(context, entry.getKey(), entry.getValue());
        }
    }
    
    public void generate(GenerationContext context, QName qname, Collection<Service> services)
        throws Exception
    {
        String name = qname.getLocalPart();
        String ns = qname.getNamespaceURI();
        
        JCodeModel model = context.getCodeModel();

        String portName = context.getDestinationPackage() + "." + name + "Service";
        portName = getUniqueName(model, portName);
        
        JDefinedClass servCls = model._class(portName);
        servCls._extends(javax.xml.ws.Service.class);
        
        String wsdlUrl = new File(context.getWsdlLocation()).toURL().toExternalForm();
        JAnnotationUse clientAnn = servCls.annotate(WebServiceClient.class);
        clientAnn.param("targetNamespace", ns);
        clientAnn.param("name", name);
        clientAnn.param("wsdlLocation", wsdlUrl);
        
        JType qnameType = model._ref(QName.class);
        
        /**
         * Constructor
         */
        JMethod constructor = servCls.constructor(JMod.PUBLIC);
        constructor._throws(MalformedURLException.class);
        
        JType urlType = model._ref(URL.class);
        
        JInvocation newURL = JExpr._new(urlType).arg(wsdlUrl);
        JInvocation newSN = JExpr._new(qnameType).arg(ns).arg(name);
        
        JInvocation superService = JExpr.invoke("super").arg(newURL).arg(newSN);
        constructor.body().add(superService);
        
        boolean addedLocal = false;
        
        JBlock staticBlock = servCls.init();
        
        JType hashMapType = model._ref(HashMap.class);
        JType mapType = model._ref(Map.class);
        JVar portsVar = servCls.field(JMod.PRIVATE + JMod.STATIC,
                                      mapType, "ports", JExpr._new(hashMapType));

        JMethod method = servCls.method(JMod.PUBLIC + JMod.STATIC, mapType, "getPortClassMap");
        method.body()._return(JExpr.ref("ports"));
        
        for (Service service : services)
        {
            JDefinedClass serviceImpl = (JDefinedClass) service.getProperty(ServiceStubGenerator.SERVICE_STUB);
            JDefinedClass serviceIntf = (JDefinedClass) service.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);
            
            QName portType = service.getServiceInfo().getPortType();
            
            JFieldVar intfClass = servCls.field(JMod.STATIC + JMod.PUBLIC,
                                                Class.class,
                                                javify(serviceIntf.name()),
                                                JExpr.dotclass(serviceIntf));

            // hack to get local support
            if (!addedLocal)
            {
                SoapBinding localBind = new SoapBinding(new QName(ns, name + "LocalBinding"), service);
                localBind.setTransportURI(LocalTransport.BINDING_ID);
                service.addBinding(localBind);
                service.addEndpoint(new QName(ns, name + "LocalPort"), localBind, "xfire.local://" + name);
                
                addedLocal = true;
            }
            
            for (Iterator itr = service.getEndpoints().iterator(); itr.hasNext();)
            {
                Endpoint endpoint = (Endpoint) itr.next();
    
                JInvocation newQN = JExpr._new(qnameType);
                newQN.arg(endpoint.getName().getNamespaceURI());
                newQN.arg(endpoint.getName().getLocalPart());
                
                JInvocation bindingQN = JExpr._new(qnameType);
                bindingQN.arg(endpoint.getBinding().getName().getNamespaceURI());
                bindingQN.arg(endpoint.getBinding().getName().getLocalPart());

                // Add a getFooEndpointMethod
                JMethod getFooEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "get" + endpoint.getName().getLocalPart());
                JBlock geBody = getFooEndpoint.body();
    
                geBody._return(JExpr.cast(serviceIntf, JExpr.direct("this").invoke("getPort").arg(newQN).arg(intfClass)));
                
                JAnnotationUse weAnn = getFooEndpoint.annotate(WebEndpoint.class);
                weAnn.param("name", endpoint.getName().getLocalPart());
                
                staticBlock.add(portsVar.invoke("put").arg(newQN).arg(intfClass));
            }
        }
    }

}
