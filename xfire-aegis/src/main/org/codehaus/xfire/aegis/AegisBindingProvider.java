package org.codehaus.xfire.aegis;

import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.Parameter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.wsdl.SchemaType;

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
    public void initialize(Service service)
    {
        String encodingStyle = (String) service.getProperty(ENCODING_URI_KEY);

        if (encodingStyle == null)
        {
            if (service.getUse().equals(SoapConstants.USE_ENCODED))
            {
                encodingStyle = service.getSoapVersion().getSoapEncodingStyle();
            }
            else
            {
                encodingStyle = SoapConstants.XSD;
            }
        }

        service.setProperty(ENCODING_URI_KEY, encodingStyle);
        final TypeMapping tm = registry.createTypeMapping(encodingStyle, true);

        service.setProperty(TYPE_MAPPING_KEY, tm);
        registry.register(service.getDefaultNamespace(), tm);
    }

    public Object readParameter(Parameter p, MessageContext context) 
        throws XFireFault
    {
        Type type = getParameterType(getTypeMapping(context.getService()), p);
        
        MessageReader reader = new ElementReader(context.getXMLStreamReader());
        
        return type.readObject(reader, context);
    }
    
    public void writeParameter(Parameter p, MessageContext context, Object value) 
        throws XFireFault
    {
        Type type = getParameterType(getTypeMapping(context.getService()), p);
        
        XMLStreamWriter writer = (XMLStreamWriter) context.getProperty(AbstractHandler.STAX_WRITER_KEY);
        MessageWriter mw = new ElementWriter(writer, p.getName());
    
        type.writeObject(value, mw, context);
    
        mw.close();
    }

    private Type getParameterType(TypeMapping tm, Parameter param)
    {
        Type type = null;
        if (param.getSchemaType() != null)
            type = tm.getType(param.getSchemaType());
        
        if (type == null)
            type = tm.getType(param.getTypeClass());
        
        return type;
    }
    
    public static TypeMapping getTypeMapping(Service service)
    {
        return (TypeMapping) service.getProperty(TYPE_MAPPING_KEY);
    }

    public SchemaType getSchemaType(Service service, Parameter param)
    {
        return getParameterType(getTypeMapping(service), param);
    }
}