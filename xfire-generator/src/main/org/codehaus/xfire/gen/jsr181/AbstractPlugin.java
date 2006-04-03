package org.codehaus.xfire.gen.jsr181;

import javax.xml.namespace.QName;

import org.codehaus.xfire.gen.GenerationContext;
import org.codehaus.xfire.util.JavaUtils;
import org.codehaus.xfire.util.NamespaceHelper;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class AbstractPlugin
{
    
    protected String javify(String name)
    {
        if (JavaUtils.isJavaKeyword(name))
            return JavaUtils.makeNonJavaKeyword(name);
        
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

    protected String getPackage(QName name, GenerationContext context)
    {
        String pckg = context.getDestinationPackage();
        
        if (pckg == null)
        {
            pckg = NamespaceHelper.makePackageName(name.getNamespaceURI());
        }
        
        return pckg;
    }
}
