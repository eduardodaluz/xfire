package org.codehaus.xfire.gen.jsr181;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class AbstractPlugin
{
    
    protected String javify(String name)
    {
        if (name.equals("null"))
            return "_null";
        
        return name;
    }

    protected String getUniqueName(JCodeModel model, String portName)
    {
        JDefinedClass servCls =  model._getClass(portName);
        if (servCls != null)
        {
            int i = 2;
            boolean cont = true;
            while (cont)
            {
                if (model._getClass(portName + i) == null)
                {
                    return portName + i;
                }
                i++;
            }
        }

        return portName;
    }
}
