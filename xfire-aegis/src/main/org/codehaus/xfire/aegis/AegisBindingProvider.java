package org.codehaus.xfire.aegis;

import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * A BindingProvider for the Aegis type system.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class AegisBindingProvider
    implements BindingProvider
{
    public static final String TYPE_MAPPING_KEY = "type.mapping";
    public static final String ENCODING_URI_KEY = "type.encodingUri";
    
    private TypeMappingRegistry registry;
    
    public AegisBindingProvider()
    {
        this(new DefaultTypeMappingRegistry(true));
    }
    
    public AegisBindingProvider(TypeMappingRegistry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Creates a type mapping for this class and registers it with the TypeMappingRegistry. This needs to be called
     * before initializeOperations().
     */
    public void initialize(Service endpoint)
    {
        String encodingStyle = (String) endpoint.getProperty(ENCODING_URI_KEY);

        if (encodingStyle == null)
        {
            AbstractBinding binding = (AbstractBinding) endpoint.getBinding();
            if (binding.getUse().equals(SoapConstants.USE_ENCODED))
            {
                encodingStyle = endpoint.getSoapVersion().getSoapEncodingStyle();
            }
            else
            {
                encodingStyle = SoapConstants.XSD;
            }
        }

        endpoint.setProperty(ENCODING_URI_KEY, encodingStyle);
        final TypeMapping tm = registry.createTypeMapping(encodingStyle, true);

        endpoint.setProperty(TYPE_MAPPING_KEY, tm);
        registry.register(endpoint.getServiceInfo().getName().getNamespaceURI(), tm);
        
        for (Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo opInfo = (OperationInfo) itr.next();
            
            initializeMessage(endpoint, opInfo.getInputMessage());
            initializeMessage(endpoint, opInfo.getOutputMessage());
        }
    }

    protected void initializeMessage(Service service, MessagePartContainer container)
    {
        for (Iterator itr = container.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
            
            part.setSchemaType(getParameterType(getTypeMapping(service), part));
        }
    }

    public Object readParameter(MessagePartInfo p, XMLStreamReader xsr, MessageContext context) 
        throws XFireFault
    {
        Type type = (Type) p.getSchemaType();

        MessageReader reader = new ElementReader(xsr);
        
        return type.readObject(reader, context);
    }
    
    public void writeParameter(MessagePartInfo p,
                               XMLStreamWriter writer,
                               MessageContext context,
                               Object value)
        throws XFireFault
    {
        Type type = (Type) p.getSchemaType();

        MessageWriter mw = new ElementWriter(writer, p.getName());

        type.writeObject(value, mw, context);
    
        mw.close();
    }

    private Type getParameterType(TypeMapping tm, MessagePartInfo param)
    {
        Type type = tm.getType(param.getName());

        if (type == null && tm.isRegistered(param.getTypeClass()))
        {
            type = tm.getType(param.getTypeClass());
        }
        
        if (type == null)
        {
            OperationInfo op = param.getContainer().getOperation();
            
            int index = -1;
            
            if (op.getInputMessage().getMessageParts().contains(param))
                index = op.getInputMessage().getMessageParts().indexOf(param);
            
            /* Note: we are not registering the type here, because it is an anonymous
             * type. Potentially there could be many schema types with this name. For example,
             * there could be many ns:in0 paramters.
             */
            type = tm.getTypeCreator().createType(op.getMethod(), index);
            type.setTypeMapping(tm);
        }

        return type;
    }
    
    public static TypeMapping getTypeMapping(Service service)
    {
        return (TypeMapping) service.getProperty(TYPE_MAPPING_KEY);
    }

}