package org.codehaus.xfire.gen.jsr181;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.local.LocalTransport;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
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
    private JFieldVar pfVar;
    private JFieldVar endpointsVar;
    private JMethod getEndpoint;
    private JMethod constructor;

    public void generate(GenerationContext context)
        throws Exception
    {
        for (Iterator itr = context.getServices().entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();
            
            generate(context, (QName) entry.getKey(), (List) entry.getValue());
        }
    }
    
    public void generate(GenerationContext context, QName name, List services)
        throws Exception
    {
        String local = name.getLocalPart();
        String ns = name.getNamespaceURI();
        
        JCodeModel model = context.getCodeModel();
        
        // Create the Client class
        String portName = getPackage(name, context) + "." + name.getLocalPart() + "Client";
        portName = javify(portName);
        
        JDefinedClass servCls = model._class(portName);

        /**
         * Constructor
         */
        constructor = servCls.constructor(JMod.PUBLIC);
        
        JType pfType = model._ref(XFireProxyFactory.class);
        pfVar = servCls.field(JMod.STATIC + JMod.PRIVATE, pfType, "proxyFactory", JExpr._new(pfType));
  
        JType hashMapType = model._ref(HashMap.class);
        endpointsVar = servCls.field(JMod.PRIVATE, hashMapType, "endpoints", JExpr._new(hashMapType));
  
        /**
         * getEndpoint(Endpoint)
         */
        JClass objectType = model.ref(Object.class);
        getEndpoint = servCls.method(JMod.PUBLIC, objectType, "getEndpoint");
        JVar epVar = getEndpoint.param(Endpoint.class, "endpoint");
        
        JBlock geBody = getEndpoint.body();
        JTryBlock tryBlock = geBody._try();
        
        JInvocation createProxy = pfVar.invoke("create");
        createProxy.arg(JExpr.direct(epVar.name()).invoke("getBinding"));
        createProxy.arg(JExpr.direct(epVar.name()).invoke("getUrl"));
        
        tryBlock.body()._return(createProxy);

        JCatchBlock catchBlock = tryBlock._catch(model.ref(MalformedURLException.class));
        JType xreType = model._ref(XFireRuntimeException.class);
        JInvocation xreThrow = JExpr._new(xreType);
        xreThrow.arg("Invalid URL");
        xreThrow.arg(catchBlock.param("e"));
        
        catchBlock.body()._throw(xreThrow);
        

        /**
         * T getEndpoint(QName)
         */
        JMethod getEndpointByName = servCls.method(JMod.PUBLIC, objectType, "getEndpoint");
        JVar epname = getEndpointByName.param(QName.class, "name");
        
        geBody = getEndpointByName.body();
        
        // Endpoint endpoint = (Endpoint) service.getEndpoint(name);
        JType endpointType = model._ref(Endpoint.class);
        JInvocation getEndpointInv = endpointsVar.invoke("get");
        getEndpointInv.arg(JExpr.direct(epname.name()));

        epVar = geBody.decl(endpointType, "endpoint", JExpr.cast(endpointType, getEndpointInv));
        
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
         * Collection getEndpoints()
         */
        JType collectionType = model.ref(Collection.class);
        JMethod getEndpoints = servCls.method(JMod.PUBLIC, collectionType, "getEndpoints");
        geBody = getEndpoints.body();
        
        geBody._return(endpointsVar.invoke("values"));
        
        // Create a Service for each Binding
        for (int i = 0; i < services.size(); i++)
        {
            Service service = (Service) services.get(i);
            
            if (service.getEndpoints().size() == 0) continue;
            
            // Add a local binding. 
            // TODO: We should have a switch for this...
            String ptName = service.getServiceInfo().getPortType().getLocalPart();
            Soap11Binding localBind = new Soap11Binding(new QName(ns, ptName + "LocalBinding"), 
                                                        LocalTransport.BINDING_ID, 
                                                        service);
            service.addBinding(localBind);
            service.addEndpoint(new QName(ns, ptName + "LocalEndpoint"), localBind, "xfire.local://" + local);
            
            JVar serviceVar = generateService(context, servCls, constructor, service, i);
            
            // Add each endpoint to the Client class
            Collection endpoints = service.getEndpoints();
            for (Iterator eitr = endpoints.iterator(); eitr.hasNext(); )
            {
                generate(context, servCls, service, serviceVar, (Endpoint) eitr.next());
            }
        }
    }
    
    protected JVar generateService(GenerationContext context, JDefinedClass servCls, 
                                   JMethod constructor, Service service, int number)
    {
        JCodeModel model = context.getCodeModel();
        JVar serviceVar = servCls.field(JMod.PRIVATE, Service.class, "service" + number);
        
        JClass serviceIntf = (JClass) service.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);
        
        /**
         * createService()
         */
        JMethod create = servCls.method(JMod.PRIVATE, void.class, "create" + number);
        
        JType asfType = model._ref(AnnotationServiceFactory.class);
        JType jsr181Type = model._ref(Jsr181WebAnnotations.class);
        JType tmType = model._ref(TransportManager.class);
        JType abSoapBindingType = model._ref(AbstractSoapBinding.class);
        JType qnameType = model._ref(QName.class);
        JType soapTransType = model._ref(SoapTransport.class);
        JType hashMapType = model._ref(HashMap.class);
        
        JVar tmVar = create.body().decl(tmType, "tm", JExpr.direct("org.codehaus.xfire.XFireFactory.newInstance().getXFire().getTransportManager()"));
        JInvocation asfCons = JExpr._new(asfType);
        asfCons.arg(JExpr._new(jsr181Type));
        asfCons.arg(tmVar);
        asfCons.arg(context.getSchemaGenerator().getBindingProviderExpr(context));
        
        JVar propsVar = create.body().decl(hashMapType, "props", JExpr._new(hashMapType));
        create.body().add(propsVar.invoke("put").arg("annotations.allow.interface").arg(JExpr.TRUE));
        
        if (service.getServiceInfo().isWrapped()) 
        {
            create.body().add(propsVar.invoke("put").arg("objectServiceFactory.style").arg(SoapConstants.STYLE_WRAPPED));
        }
        else
        {
            create.body().add(propsVar.invoke("put").arg("objectServiceFactory.style").arg(SoapConstants.STYLE_DOCUMENT));
        }
        
        JVar asfVar = create.body().decl(asfType, "asf", asfCons);
        JInvocation createInvoke = asfVar.invoke("create");
        
        createInvoke.arg(JExpr.direct(serviceIntf.fullName() + ".class"));
        createInvoke.arg(propsVar);
        
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

        constructor.body().invoke(create);

        return serviceVar;
    }

    protected void generate(GenerationContext context, JDefinedClass servCls, 
                            Service service, JVar serviceVar, Endpoint endpoint)
        throws Exception
    {
        String name = service.getSimpleName();
        String ns = service.getTargetNamespace();

        JCodeModel model = context.getCodeModel();
        
        /*
         * Add endpoints to constructor
         */
        JBlock consBody = constructor.body();
        JType qnameType = model.ref(QName.class);
        
        JDefinedClass serviceIntf = (JDefinedClass) service.getProperty(ServiceInterfaceGenerator.SERVICE_INTERFACE);

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

        JType endpointType = model.ref(Endpoint.class);
        JVar epVar = consBody.decl(endpointType, endpoint.getName().getLocalPart() + "EP", addEndpointInv);

        JInvocation addEndpoint = endpointsVar.invoke("put").arg(newQN).arg(epVar);
        consBody.add(addEndpoint);
        
        // Add a getFooEndpointMethod
        JMethod getFooEndpoint = servCls.method(JMod.PUBLIC, serviceIntf, "get"
                + endpoint.getName().getLocalPart());
        JBlock geBody = getFooEndpoint.body();

        geBody._return(JExpr.cast(serviceIntf, JExpr.direct("this").invoke(getEndpoint).arg(newQN)));

        JMethod getFooEndpoint1 = servCls.method(JMod.PUBLIC, serviceIntf, "get"
                + endpoint.getName().getLocalPart());
        getFooEndpoint1.param(String.class,"url");
        JBlock geBody1 = getFooEndpoint1.body();
        JInvocation getEndp = JExpr.invoke(getFooEndpoint);
        JVar tpe = geBody1.decl(serviceIntf, "var", getEndp );
        
        geBody1.directStatement("org.codehaus.xfire.client.Client.getInstance(var).setUrl(url);");
        geBody1._return(tpe);
    }

}
