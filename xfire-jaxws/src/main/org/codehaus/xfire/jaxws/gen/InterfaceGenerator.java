package org.codehaus.xfire.jaxws.gen;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.gen.GenerationException;
import org.codehaus.xfire.gen.jsr181.ServiceInterfaceGenerator;
import org.codehaus.xfire.service.MessagePartInfo;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

public class InterfaceGenerator
    extends ServiceInterfaceGenerator
{

    @Override
    protected void generateExceptionClass(GenerationContext context, MessagePartInfo part, JMethod method) 
        throws GenerationException
    {
        JCodeModel model = context.getCodeModel();
        
        JType sType = model._ref(String.class);
        JType tType = model._ref(Throwable.class);

        try
        {
            JDefinedClass fault = 
                model._class(context.getDestinationPackage() + "." + 
                             javify(part.getName().getLocalPart()) + "_Exception");
            
            fault._extends(Exception.class);
            
            JMethod con = fault.constructor(JMod.PUBLIC);
            con.param(sType, "msg");
        }
        catch (JClassAlreadyExistsException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
