package org.codehaus.xfire.xmlbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.service.object.DefaultObjectService;
import org.codehaus.xfire.service.object.ObjectServiceFactory;
import org.codehaus.xfire.service.object.Operation;
import org.codehaus.xfire.service.object.Parameter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.type.TypeMappingRegistry;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class XMLBeansServiceBuilder
    extends ObjectServiceFactory
{
    public XMLBeansServiceBuilder( TransportManager transportManager, TypeMappingRegistry registry )
    {
        super( transportManager, registry );
    }

    protected void addOperation(DefaultObjectService service, Method method)
    {
        final Operation op = new Operation( method );
        
        service.addOperation(op);
        
        Class[] paramClasses = method.getParameterTypes();
        
        boolean isDoc = service.getStyle().equals(SoapConstants.STYLE_DOCUMENT);
        
        Parameter p = null;
        
        for ( int j = 0; j < paramClasses.length; j++ )
        {
            if ( XmlObject.class.isAssignableFrom(paramClasses[j]) )
            {
                SchemaType st = getSchemaType(paramClasses[j]);

                p = new Parameter(st.getDocumentElementName(), paramClasses[j]);
                service.getTypeMapping().register(paramClasses[j], st.getDocumentElementName(), new XMLBeansType());
            }
            else
            {
                String paramName = "";
                if (isDoc)
                    paramName = method.getName();

                paramName = paramName + "in" + j;

                final QName q = new QName(service.getDefaultNamespace(), paramName);
                p = new Parameter(q, paramClasses[j]);
            }
            
            op.addInParameter( p );
        }

        Parameter outP = null;
        
        Class outClass = op.getMethod().getReturnType();
        if (!outClass.isAssignableFrom(void.class))
        {
            if ( XmlObject.class.isAssignableFrom(op.getMethod().getReturnType()) )
            {
                SchemaType st = getSchemaType(outClass);
    
                outP = new Parameter(st.getDocumentElementName(), outClass);
                service.getTypeMapping().register(outClass, st.getDocumentElementName(), new XMLBeansType());
            }
            else
            {
                String outName = "";
                if ( isDoc )
                    outName = method.getName();
                
                final QName q = new QName(service.getDefaultNamespace(), outName + "out");
                outP = new Parameter(q, method.getReturnType());
            }
            
            op.addOutParameter(outP);
        }
    }
    
    /**
     * Introspect to find the SchemaType for a particular XMLBeans class.
     */
    protected SchemaType getSchemaType(Class clazz)
    {
        try
        {
            Field f = clazz.getDeclaredField("type");
    
            return (SchemaType) f.get(null);
        }
        catch (NoSuchFieldException e)
        {
            throw new XFireRuntimeException("Couldn't find type field!", e);
        } 
        catch (IllegalArgumentException e)
        {
            throw new XFireRuntimeException("Couldn't get type field!", e);
        }
        catch (IllegalAccessException e)
        {
            throw new XFireRuntimeException("Couldn't get type field!", e);
        }
    }
}
