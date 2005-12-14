package org.codehaus.xfire.gen.jsr181;

import java.net.MalformedURLException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.jsr181.Jsr181WebAnnotations;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GeneratorPlugin;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.Soap11Binding;
import org.codehaus.xfire.soap.Soap12Binding;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.local.LocalTransport;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class PortGenerator
    extends AbstractPlugin
    implements GeneratorPlugin
{
    public void generate(GenerationContext context)
        throws Exception
    {
        for (Iterator itr = context.getServices().iterator(); itr.hasNext();)
        {
            generate(context, (Service) itr.next());
        }
    }
    
    public void generate(GenerationContext context, Service service)
        throws Exception
    {
        String name = service.getSimpleName();
        String ns = service.getTargetNamespace();
        
        if (service.getEndpoints().size() == 0) return;
        
        // hack to get local support
        Soap11Binding localBind = new Soap11Binding(new QName(ns, name + "LocalBinding"), 
                                                    LocalTransport.BINDING_ID, 
                                                    service);
        service.addBinding(localBind);
        service.addEndpoint(new QName(ns, name + "LocalPort"), localBind, "xfire.local://" + name);
        
        JCodeModel model = context.getCodeModel();

        String portName = context.getDestinationPackage() + "." + service.getName().getLocalPart() + "Client";
        portName = getUniqueName(model, portName);
        JDefinedClass servCls = model._class(portName);
        
        JVar serviceVar = servCls.field(JMod.PRIVATE, Service.class, "service");
        
        JDefinedClass serviceImpl = (JDefinedClass) service.getProperty(ServiceStubGenerator.SERVICE_STUB);
        JDefinedClass serviceIntf = (JDefinedClass) service.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);
        
        JType pfType = model._ref(XFireProxyFactory.class);
        JFieldVar pfVar = servCls.field(JMod.STATIC + JMod.PRIVATE, pfType, "proxyFactory", JExpr._new(pfType));
  
        /**
         * createService()
         */
        JMethod create = servCls.method(JMod.PRIVATE, void.class, "createService");
        
        JType asfType = model._ref(AnnotationServiceFactory.class);
        JType jsr181Type = model._ref(Jsr181WebAnnotations.class);
        JType tmType = model._ref(TransportManager.class);
        JType abSoapBindingType = model._ref(AbstractSoapBinding.class);
        JType qnameType = model._ref(QName.class);
        JType soapTransType = model._ref(SoapTransport.class);
        
        JVar tmVar = create.body().decl(tmType, "tm", JExpr.direct("org.codehaus.xfire.XFireFactory.newInstance().getXFire().getTransportManager()"));
        JInvocation asfCons = JExpr._new(asfType);
        asfCons.arg(JExpr._new(jsr181Type));
        asfCons.arg(tmVar);
        asfCons.arg(context.getSchemaGenerator().getBindingProviderExpr(context));
        
        JVar asfVar = create.body().decl(asfType, "asf", asfCons);
        JInvocation createInvoke = asfVar.invoke("create");
        
        createInvoke.arg(JExpr.direct(serviceImpl.fullName() + ".class"));
        
        JInvocation bindingCreation = asfVar.invoke("setBindingCreationEnabled");
        bindingCreation.arg(JExpr.lit(true));
        create.body().add(bindingCreation);
        
        JType serviceType = model._ref(Service.class);
        create.body().assign(serviceVar, createInvoke);
        
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Binding binding = (Binding) itr.next();
            if (!(binding instanceof AbstractSoapBinding)) continue;
            
            AbstractSoapBinding soapBinding = (AbstractSoapBinding) binding;

            JBlock block = create.body().block();
            
            JInvocation createBinding;
            if (soapBinding instanceof Soap12Binding)
            {
                createBinding = asfVar.invoke("createSoap12Binding");
            }
            else
            {
                createBinding = asfVar.invoke("createSoap11Binding");
            }
            
            createBinding.arg(serviceVar);
            
            JInvocation newQN = JExpr._new(qnameType);
            newQN.arg(soapBinding.getName().getNamespaceURI());
            newQN.arg(soapBinding.getName().getLocalPart());
            createBinding.arg(newQN);
            createBinding.arg(soapBinding.getBindingId());

            JVar sbVar = block.decl(abSoapBindingType, "soapBinding", createBinding);
        }
        
        /**
         * Constructor
         */
        JMethod constrcutor = servCls.constructor(JMod.PUBLIC);
        constrcutor.body().invoke(create);
        
        /**
         * addEndpoint()
         */
        JMethod addEndpointMethod = servCls.method(JMod.PUBLIC, void.class, "addEndpoint");
        JVar epname = addEndpointMethod.param(QName.class, "name");
        JVar bindingId = addEndpointMethod.param(QName.class, "binding");
        JVar address = addEndpointMethod.param(String.class, "address");
        
        JInvocation addEPInvoke = serviceVar.invoke("addEndpoint");
        addEPInvoke.arg(JExpr.direct(epname.name()));
        addEPInvoke.arg(JExpr.direct(bindingId.name()));
        addEPInvoke.arg(JExpr.direct(address.name()));
        
        addEndpointMethod.body().add(addEPInvoke);
        
        /**
         * T getEndpoint(Endpoint)
         */
        JMethod getEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "getEndpoint");
        JVar epVar = getEndpoint.param(Endpoint.class, "endpoint");
        
        JBlock geBody = getEndpoint.body();
        JTryBlock tryBlock = geBody._try();
        
        JInvocation createProxy = pfVar.invoke("create");
        createProxy.arg(JExpr.direct(epVar.name()).invoke("getBinding"));
        createProxy.arg(JExpr.direct(epVar.name()).invoke("getUrl"));
        
        tryBlock.body()._return(JExpr.cast(serviceIntf, createProxy));
        
        JCatchBlock catchBlock = tryBlock._catch(model.ref(MalformedURLException.class));
        JType xreType = model._ref(XFireRuntimeException.class);
        JInvocation xreThrow = JExpr._new(xreType);
        xreThrow.arg("Invalid URL");
        xreThrow.arg(catchBlock.param("e"));
        
        catchBlock.body()._throw(xreThrow);
        
        /**
         * T getEndpoint(QName)
         */
        JMethod getEndpointByName = servCls.method(JMod.PUBLIC, serviceIntf, "getEndpoint");
        epname = getEndpointByName.param(QName.class, "name");
        
        geBody = getEndpointByName.body();
        
        // Endpoint endpoint = (Endpoint) service.getEndpoint(name);
        JType endpointType = model._ref(Endpoint.class);
        JInvocation getEndpointInv = serviceVar.invoke("getEndpoint");
        getEndpointInv.arg(JExpr.direct(epname.name()));

        epVar = geBody.decl(endpointType, "endpoint", getEndpointInv);
        
        // if (endpoint == null)
        JBlock noEPBlock = geBody._if(JExpr.direct(epVar.name()).eq(JExpr._null()))._then();
        
        // throw IllegalStateException
        JType iseType = model._ref(IllegalStateException.class);
        JInvocation iseThrow = JExpr._new(iseType);
        iseThrow.arg("No such endpoint!");
        noEPBlock._throw(iseThrow);
        
        // return endpoint
        
        JInvocation geInvoke = JExpr.invoke(getEndpoint);
        geInvoke.arg(JExpr.direct(epVar.name()));
        geBody._return(geInvoke);
        
        /**
         * T getEndpoint()
         */
        JMethod getDefaultEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "getEndpoint");
        geBody = getDefaultEndpoint.body();
        
        JBlock noEPs = geBody._if(serviceVar.invoke("getEndpoints").invoke("size").eq(JExpr.lit(0)))._then();
        
        iseThrow = JExpr._new(iseType);
        iseThrow.arg("No available endpoints!");
        noEPs._throw(iseThrow);
        
        epVar = geBody.decl(endpointType, "endpoint", 
                            JExpr.cast(endpointType, serviceVar.invoke("getEndpoints").invoke("iterator").invoke("next")));
        
        getEndpointInv = JExpr.direct("this").invoke(getEndpoint);
        getEndpointInv.arg(JExpr.direct("endpoint"));
        
        geBody._return(getEndpointInv);
        
        /*
         * Add endpoints to constructor
         */
        JBlock consBody = constrcutor.body();
        
        for (Iterator itr = service.getEndpoints().iterator(); itr.hasNext();)
        {
            Endpoint endpoint = (Endpoint) itr.next();
            
            JInvocation addEndpointInv = serviceVar.invoke("addEndpoint");
            JInvocation newQN = JExpr._new(qnameType);
            newQN.arg(endpoint.getName().getNamespaceURI());
            newQN.arg(endpoint.getName().getLocalPart());
            
            JInvocation bindingQN = JExpr._new(qnameType);
            bindingQN.arg(endpoint.getBinding().getName().getNamespaceURI());
            bindingQN.arg(endpoint.getBinding().getName().getLocalPart());
 
            addEndpointInv.arg(newQN);
            addEndpointInv.arg(bindingQN);
            addEndpointInv.arg(endpoint.getUrl());
            
            consBody.add(addEndpointInv);
            
//          Add a getFooEndpointMethod
            JMethod getFooEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "get" + endpoint.getName().getLocalPart());
            geBody = getFooEndpoint.body();

            geBody._return(JExpr.direct("this").invoke(getEndpoint).arg(newQN));
        }
    }

}
