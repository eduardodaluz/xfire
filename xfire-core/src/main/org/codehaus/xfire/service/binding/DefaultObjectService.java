package org.codehaus.xfire.service.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;

import org.codehaus.xfire.service.MessageService;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilder;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultObjectService
    extends MessageService
    implements ObjectService
{
    private List allowedMethods;

    private Class serviceClass;

    private Hashtable operations;

    private WSDLBuilder wsdlBuilder;

    private boolean autoTyped = false;

    private int scope = ObjectService.SCOPE_APPLICATION;

    private String encodingStyleURI;

    private Invoker invoker;

    public DefaultObjectService()
    {
        super();
        this.allowedMethods = new ArrayList();
        this.operations = new Hashtable();
    }

    /**
     * @param className
     */
    public void setServiceClass(final String className)
        throws ClassNotFoundException
    {
        setServiceClass(loadClass(className));
    }

    /**
     * @param serviceClass
     */
    public void setServiceClass(final Class serviceClass)
    {
        this.serviceClass = serviceClass;
    }

    public void addOperation(final Operation op)
    {
        operations.put(op.getName(), op);
    }

    /**
     * Determines whether or not to expose the specified method.
     * 
     * @param methodName
     */
    private boolean isAllowed(final String methodName)
    {
        return (allowedMethods.isEmpty() || allowedMethods.contains(methodName));
    }

    public Operation getOperation(final String localName)
    {
        return (Operation) operations.get(localName);
    }

    public Collection getOperations()
    {
        return operations.values();
    }

    protected Map getOperationsMap()
    {
        return operations;
    }

    public List getAllowedMethods()
    {
        return allowedMethods;
    }

    /**
     * @param allowedMethods
     *            The allowedMethods to set.
     */
    public void setAllowedMethods(final List allowedMethods)
    {
        this.allowedMethods = allowedMethods;
    }

    /**
     * @return
     */
    public Class getServiceClass()
    {
        return serviceClass;
    }

    /**
     * Load a class from the class loader.
     * 
     * @param className
     *            The name of the class.
     * 
     * @return The class.
     */
    protected Class loadClass(final String className)
        throws ClassNotFoundException
    {
        try
        {
            return getClass().getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            try
            {
                return Class.forName(className);
            }
            catch (ClassNotFoundException cnf2)
            {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            }
        }
    }

    public WSDLWriter getWSDLWriter()
        throws WSDLException
    {
        final WSDLWriter writer = super.getWSDLWriter();

        if (writer == null)
        {
            final WSDLBuilder b = getWSDLBuilder();

            if (b != null)
                return getWSDLBuilder().createWSDLWriter(this);
        }

        return writer;
    }

    public int getScope()
    {
        return scope;
    }

    public void setScope(final int scope)
    {
        this.scope = scope;
    }

    public WSDLBuilder getWSDLBuilder()
    {
        return wsdlBuilder;
    }

    public void setWSDLBuilder(final WSDLBuilder wsdlBuilder)
    {
        this.wsdlBuilder = wsdlBuilder;
    }

    public Invoker getInvoker()
    {
        return invoker;
    }

    public void setInvoker(Invoker invoker)
    {
        this.invoker = invoker;
    }
}
