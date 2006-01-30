package org.codehaus.xfire.gen.jaxb;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.wsdl.Definition;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.SchemaSupport;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.w3c.dom.Element;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.api.XJC;

public class JAXBSchemaSupport implements SchemaSupport
{
    SchemaCompiler schemaCompiler;
    
    private AegisBindingProvider provider = new AegisBindingProvider(new JaxbTypeRegistry());

    private S2JJAXBModel model;
    private JCodeModel jaxbModel;
    
    public void initialize(GenerationContext context) throws Exception
    {
        // Gross hack to get around NPE
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0], cl));

        ErrorReceiverImpl er = new ErrorReceiverImpl();
         
        schemaCompiler = XJC.createSchemaCompiler();
        schemaCompiler.setErrorListener(er);

        Definition def = (Definition) context.getWsdl();
        
        ArrayList<Element> elements = new ArrayList<Element>();
        for (Iterator itr = def.getTypes().getExtensibilityElements().iterator(); itr.hasNext();)
        {
            Object o = itr.next();
            if (o instanceof UnknownExtensibilityElement)
            {
                UnknownExtensibilityElement uee = (UnknownExtensibilityElement) o;
                
                elements.add(uee.getElement());
            }
            else
            {
                try 
                {
                    Method mth = o.getClass().getMethod("getElement", new Class[0]);
                    Object val = mth.invoke(o, new Object[0]);
                    elements.add((Element) val);
                } catch (Exception e) {
                    // Ignore exceptions ?
                }
            }
        }
        
        int schemaElementCount = 1;
        for(Element schemaElement : elements)
        {
            String systemId = new String("#types?schema"+schemaElementCount++);
            schemaCompiler.parseSchema(systemId,schemaElement);
        }
        
        model = schemaCompiler.bind();

        //feed external jaxb:bindings file
        /*Set<InputSource> externalBindings = ((WSDLModelInfo)_modelInfo).getJAXBBindings();
        if(externalBindings != null){
            for(InputSource jaxbBinding : externalBindings){
                schemaCompiler.parseSchema(jaxbBinding);
            }
        }*/
        
        jaxbModel = model.generateCode(null, er);
         jaxbModel.build(context.getOutputDirectory());

        Thread.currentThread().setContextClassLoader(cl);
    }

    public JType getType(GenerationContext context, QName concreteType, QName schemaType) 
        throws GenerationException
    {
        JCodeModel codeModel = context.getCodeModel();

        Mapping mapping = model.get(concreteType);
        
        if (mapping == null)
        {
            mapping = model.get(schemaType);
        }
        
        JType typeClass = null;
        
        if (mapping != null)
        {
            typeClass = mapping.getType().getTypeClass();
        }
        
        if(typeClass == null)
        {
        	TypeAndAnnotation def = model.getJavaType(concreteType);
        	if(def == null)
        	{
        		def = model.getJavaType(schemaType);
        	}
        	if(def != null)
        	{
        		return def.getTypeClass();
        	}
        }
        
        if (typeClass == null)
        {
            Class clazz = provider.getTypeClass(concreteType, null);
            if (clazz == null)
            {
                clazz = provider.getTypeClass(schemaType, null);
            }
            
            if (clazz == null)
            {
                throw new GenerationException("Could not find type " + concreteType);
            }
            
            typeClass = codeModel._ref(clazz);
        }
        
        return typeClass;
    }

    public BindingProvider getBindingProvider()
    {
        return provider;
    }

    public JExpression getBindingProviderExpr(GenerationContext context)
    {
        JType aegis = context.getCodeModel()._ref(AegisBindingProvider.class);
        JInvocation expr = JExpr._new(aegis);
        
        JType xbean = context.getCodeModel()._ref(JaxbTypeRegistry.class);
        expr.arg(JExpr._new(xbean));
        
        return expr;
    }
}
