package org.codehaus.xfire.xmlbeans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.java.JavaService;
import org.codehaus.xfire.java.Operation;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceHandler
	extends AbstractXMLBeansHandler
{
    public XmlObject[] invoke(XmlObject[] request, MessageContext context)
    	throws Exception
    {
        JavaService service = (JavaService) context.getService();
        
        Object serviceObject = service.getServiceObject(context);
        
        Method method = findMethod(service, request);

        Object res = method.invoke(serviceObject, (Object[]) request);
        
        if ( res.getClass().isArray() )
	        return (XmlObject[]) res;
        else
            return new XmlObject[] { (XmlObject) res };
    }

    /**
     * @param serviceClass
     * @throws XFireFault
     */
    private Method findMethod(JavaService service, XmlObject[] request) 
    	throws XFireFault
    {
        Collection operations = service.getOperations();

        for ( Iterator itr = operations.iterator(); itr.hasNext(); )
        {
            Operation op = (Operation) itr.next();
            
            boolean found = true;
            
            Class[] params = op.getMethod().getParameterTypes();
            if ( params.length == request.length )
            {
	            for ( int j = 0; j < params.length; j++ )
	            {
	                if ( !params[j].isAssignableFrom(request[j].getClass()) )
	                    found = false;
	            }
            }
            else
            {
                System.out.println("not matching params");
                found = false;
            }
            
            if ( found )
                return op.getMethod();
        }
        
        throw new XFireFault("Couldn't find an appropriate operation!", XFireFault.SENDER);
    }

}
