package org.codehaus.xfire.aegis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.jdom.JDOMReader;
import org.codehaus.xfire.aegis.jdom.JDOMWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageHeaderInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.AbstractBinding;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.soap.SoapConstants;
import org.jdom.Element;
import org.jdom.Namespace;

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
    
    private static final int IN_PARAM = 0;
    private static final int OUT_PARAM = 1;
    private static final int FAULT_PARAM = 2;
    
    private TypeMappingRegistry registry;

    private Map part2type = new HashMap();
    
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
        for (Iterator itr = endpoint.getServiceInfo().getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo opInfo = (OperationInfo) itr.next();
            try
            {
                initializeMessage(endpoint, opInfo.getInputMessage(), IN_PARAM);
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing parameters for method " + opInfo.getMethod());
                throw e;
            }
            
            try
            {
                initializeMessage(endpoint, opInfo.getOutputMessage(), OUT_PARAM);
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing return value for method " + opInfo.getMethod());
                throw e;
            }
            
            try
            {
                for (Iterator faultItr = opInfo.getFaults().iterator(); faultItr.hasNext();)
                {
                    FaultInfo info = (FaultInfo) faultItr.next();
                    initializeMessage(endpoint, info, FAULT_PARAM);
                }
            }
            catch(XFireRuntimeException e)
            {
                e.prepend("Error initializing return value for method " + opInfo.getMethod());
                throw e;
            }
        }
    }

    protected void initializeMessage(Service service, MessagePartContainer container, int type)
    {
        for (Iterator itr = container.getMessageHeaders().iterator(); itr.hasNext();)
        {
            MessageHeaderInfo header = (MessageHeaderInfo) itr.next();
            
            header.setSchemaType(getParameterType(getTypeMapping(service), header, type));
        }
        
        for (Iterator itr = container.getMessageParts().iterator(); itr.hasNext();)
        {
            MessagePartInfo part = (MessagePartInfo) itr.next();
            
            part.setSchemaType(getParameterType(getTypeMapping(service), part, type));
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

        MessageWriter mw;
        boolean writeOuter = type.isWriteOuter();
        if (writeOuter)
            mw = new ElementWriter(writer, p.getName());
        else
            mw = new ElementWriter(writer);

        type.writeObject(value, mw, context);
    
        if (writeOuter)
            mw.close();
    }

    public QName getSuggestedName(Service service, OperationInfo op, int param)
    {
        TypeMapping tm = getTypeMapping(service);
        Type type = tm.getTypeCreator().createType(op.getMethod(), param);
        
        if (type.isComplex() && !type.isAbstract()) 
            return type.getSchemaType();
        
        return null;
    }

    private Type getParameterType(TypeMapping tm, MessagePartInfo param, int paramtype)
    {
        Type type = tm.getType(param.getName());
        
        if (type == null)
        {
            type = (Type) part2type.get(param);
        }

        /*if (type == null && tm.isRegistered(param.getTypeClass()))
        {
            type = tm.getType(param.getTypeClass());
            part2type.put(param, type);
        }*/

        if (type == null)
        {
            OperationInfo op = param.getContainer().getOperation();
            
            if (paramtype != FAULT_PARAM)
            {
                int index;
                if (paramtype == IN_PARAM)
                {
                    index = param.getIndex();
                }
                else
                {
                    index = -1 - param.getIndex();
                }
                
                /* Note: we are not registering the type here, because it is an anonymous
                 * type. Potentially there could be many schema types with this name. For example,
                 * there could be many ns:in0 paramters.
                 */
                type = tm.getTypeCreator().createType(op.getMethod(), index);
            }
            else
            {
                type = tm.getTypeCreator().createType(param.getTypeClass());
            }
            
            type.setTypeMapping(tm);
            part2type.put(param, type);
        }

        return type;
    }

    private Type getParameterType(TypeMapping tm, MessageHeaderInfo param, int paramtype)
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
            
            if (op.getInputMessage().getMessageHeaders().contains(param))
                index = param.getIndex();
            
            /* Note: we are not registering the type here, because it is an anonymous
             * type. Potentially there could be many schema types with this name. For example,
             * there could be many ns:in0 paramters.
             */
            type = tm.getTypeCreator().createType(op.getMethod(), index);
            type.setTypeMapping(tm);
        }

        return type;
    }
    
    public TypeMapping getTypeMapping(Service service)
    {
        TypeMapping tm = (TypeMapping) service.getProperty(TYPE_MAPPING_KEY);
        
        if (tm == null) tm = createTypeMapping(service);
        
        return tm;
    }

    protected TypeMapping createTypeMapping(Service endpoint)
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
        
        return tm;
    }

    public Object readHeader(MessageHeaderInfo p, MessageContext context)
        throws XFireFault
    {
        Type type = (Type) p.getSchemaType();

        QName name = p.getName();
        Element headers = context.getExchange().getInMessage().getHeader();
        Element header = headers.getChild(name.getLocalPart(), 
                                          Namespace.getNamespace(name.getNamespaceURI()));
        
        if (header == null) return null;
        
        return type.readObject(new JDOMReader(header), context);
    }

    public void writeHeader(MessagePartInfo p, MessageContext context, Object value)
        throws XFireFault
    {
        Type type = (Type) p.getSchemaType();

        MessageWriter mw = new JDOMWriter(context.getOutMessage().getHeader());

        type.writeObject(value, mw, context);
    
        mw.close();
    }

}