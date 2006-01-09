package org.codehaus.xfire.jaxws.gen;

import javax.xml.ws.Holder;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.service.MessagePartInfo;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;

public class ServiceStubGenerator
    extends org.codehaus.xfire.gen.jsr181.ServiceStubGenerator
{
    protected JType getHolderType(GenerationContext context, MessagePartInfo part)
        throws GenerationException
    {
        JType genericType = super.getHolderType(context, part);
        
        JClass holder = context.getCodeModel().ref(Holder.class);
        holder = holder.narrow((JClass) genericType);
        return holder;
    }
}
