package org.codehaus.xfire.xmlbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.java.DefaultJavaService;
import org.codehaus.xfire.java.Operation;
import org.codehaus.xfire.java.Parameter;
import org.codehaus.xfire.java.type.Type;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 13, 2004
 */
public class XMLBeansService
	extends DefaultJavaService
{
    protected void addOperation(Method method)
    {
        Operation op = new Operation(method, this);
        
        getOperationsMap().put( method.getName(), op );
        
        Class[] paramClasses = method.getParameterTypes();
        
        boolean isDoc = getStyle().equals(SoapConstants.STYLE_DOCUMENT);
        
        for ( int j = 0; j < paramClasses.length; j++ )
        {
            Parameter p = null;
            
            if ( XmlObject.class.isAssignableFrom(paramClasses[j]) )
            {
                SchemaType st = getSchemaType(paramClasses[j]);

                p = new XMLBeansParameter(st);
            }
            else
            {
                String paramName = "";
                if ( isDoc )
                    paramName = method.getName();
                
                paramName = paramName + "in" + j;
                
                String ns = getDefaultNamespace();
                Type t = getTypeMapping().getType(paramClasses[j]);
                
                if ( t.isComplex() )
                    ns = t.getSchemaType().getNamespaceURI();
                
                QName q = new QName(ns, paramName);
                p = new Parameter(q, t);
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
    
                outP = new XMLBeansParameter(st);
            }
            else
            {
                String outName = "";
                if ( isDoc )
                    outName = method.getName();
                
                String ns = getDefaultNamespace();
                Type t = getTypeMapping().getType(method.getReturnType());
                
                if ( t.isComplex() )
                    ns = t.getSchemaType().getNamespaceURI();
                        
                QName q = new QName(ns, outName + "out");
                outP = new Parameter(q, t);
            }
            
            op.addOutParameter(outP);
        }
    }

    /**
     * Introspect to find the SchemaType for a particular XMLBeans class.
     */
    private SchemaType getSchemaType(Class clazz)
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
