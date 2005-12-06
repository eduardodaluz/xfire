package org.codehaus.xfire.jaxws.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.ws.WebFault;
import javax.xml.ws.WebServiceException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.jdom.JDOMWriter;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.CustomFaultHandler;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.stax.ElementStreamWriter;

/**
 * Takes an exception that is a JAX-WS {@lit @}WebFault and serializes it
 * appropriately via JAXB.
 * 
 * @author Dan Diephouse
 */
public class WebFaultHandler
    extends CustomFaultHandler
{
    protected void handleFault(MessageContext context,
                               XFireFault fault,
                               Throwable cause,
                               MessagePartInfo faultPart)
        throws XFireFault
    {        
        Method m;
        Object val;
        try
        {
            m = cause.getClass().getMethod("getFaultInfo", new Class[0]);
            val = m.invoke(cause, new Object[0]);
        }
        catch (Exception e)
        {
            throw new WebServiceException("Could not get FaultInfo.", e);
        }
 
        WebFault webFault = (WebFault) faultPart.getTypeClass().getAnnotation(WebFault.class);
        
        Class clazz = null;
        try
        {
            if (webFault.faultBean().length() > 0)
                clazz = ClassLoaderUtils.loadClass(webFault.faultBean(), getClass());
        }
        catch (ClassNotFoundException e)
        {
            throw new WebServiceException("Could not find fault bean class " + webFault.faultBean());
        }
        
        if (clazz == null)
        {
            clazz = m.getReturnType();
        }
        
        AegisBindingProvider provider = ((AegisBindingProvider) context.getService().getBindingProvider());
        
        Type type = provider.getType(context.getService(), clazz);
        if (type == null)
        {
            throw new WebServiceException("Could not find type for class " + clazz);
        }
        
        type.writeObject(val, new ElementWriter(new ElementStreamWriter(fault.getDetail())), context);
    }
}
