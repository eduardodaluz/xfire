package org.codehaus.xfire.gen.xmlbeans;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.SchemaSupport;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.xmlbeans.XmlBeansTypeRegistry;
import org.jdom.Element;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;

public class XmlBeansSchemaSupport
    implements SchemaSupport
{
    SchemaTypeLoader loader = 
        XmlBeans.typeLoaderForClassLoader(XmlObject.class.getClassLoader());

    private DefaultTypeMappingRegistry reg = new XmlBeansTypeRegistry();
    private AegisBindingProvider provider = new AegisBindingProvider(reg);
    private TypeMapping tm;
    
    public XmlBeansSchemaSupport()
    {
        reg.createDefaultMappings();
        tm = reg.getDefaultTypeMapping();
    }

    public void initialize(GenerationContext context)
    {
     	
    }
    
	public JType getType(GenerationContext context, MessagePartInfo part)
			throws GenerationException {

		QName concreteType = part.getName();
		QName schemaType = part.getSchemaType().getSchemaType();
		
		return getType(context, concreteType, schemaType);
    }

    public JType getType(GenerationContext context, QName concreteType, QName schemaType) 
        throws GenerationException
    {
   
        SchemaType xst = loader.findDocumentType(concreteType);
        
        if (xst == null)
        {    
            xst = loader.findDocumentType(schemaType);
        }
        
        if (xst != null)
        {    
            return context.getCodeModel().ref(xst.getJavaClass());
        }
        else 
        {
            Class cls;
            Type type = tm.getType(schemaType);
            cls = (type != null) ? cls = type.getTypeClass() : XmlObject.class;
            
            return context.getCodeModel().ref(cls);
        }
    }
    
    public BindingProvider getBindingProvider()
    {
        return provider;
    }

    public JExpression getBindingProviderExpr(GenerationContext context)
    {
        JType aegis = context.getCodeModel()._ref(AegisBindingProvider.class);
        JInvocation expr = JExpr._new(aegis);
        
        JType xbean = context.getCodeModel()._ref(XmlBeansTypeRegistry.class);
        expr.arg(JExpr._new(xbean));
        
        return expr;//JExpr._new()
    }
    
    public String getServiceFactory()
    {
        return "#xmlbeansServiceFactory";
    }
    
    public Element getServiceFactoryBean()
    {
        return null;
    }
}
