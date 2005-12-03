package org.codehaus.xfire.gen;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.binding.BindingProvider;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;

public interface SchemaSupport
{
    void initialize(GenerationContext context) throws Exception;
    
    JType getType(GenerationContext context, QName concreteType, QName schemaType) 
        throws GenerationException;

    JExpression getBindingProviderExpr(GenerationContext context);

    BindingProvider getBindingProvider();
}
