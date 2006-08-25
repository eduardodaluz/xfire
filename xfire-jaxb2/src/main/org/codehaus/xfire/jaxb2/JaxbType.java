package org.codehaus.xfire.jaxb2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.Resolver;
import org.codehaus.xfire.util.stax.DOMStreamWriter;
import org.jdom.Element;
import org.xml.sax.SAXException;



public class JaxbType
    extends Type
{
	public static final String SEARCH_PACKAGES = "jaxb.search.pacakges";
    public static final String ENABLE_VALIDATION = "jaxb.enable.validatation";
    public static final String VALIDATION_SCHEMA = "jaxb.validation.schema";
    public static final String GENERATED_VALIDATION_SCHEMA = "jaxb.generated.validation.schema";
    
    
    private static final QName XSI_TYPE = new QName(SoapConstants.XSI_NS, "type");
    
    SchemaFactory schemaFactory = null;
    private JAXBContext context;

    public JaxbType(Class clazz)
    {
        setTypeClass(clazz);

        initType();
    }

     private void setupValidationSchema(Collection<String> schemaLocations,Unmarshaller u ) throws IOException, SAXException {
        
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StreamSource[] schemaSources = new StreamSource[schemaLocations.size()];

        try {
            int i = 0;
            for (Iterator itr = schemaLocations.iterator(); itr.hasNext();) {
                String schemaLocation = (String) itr.next();
                schemaSources[i] = new StreamSource(new Resolver(schemaLocation).getInputStream());
                i++;
            }
            
            u.setSchema(factory.newSchema(schemaSources));
        }
        /** make sure to close all ressources * */
        finally {
            for (int i = 0; i < schemaSources.length; i++) {
                if (schemaSources[i] != null) {
                    InputStream inputStream = schemaSources[i].getInputStream();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
        

    }

    /**
     * @param context
     * @param u
     */
    @SuppressWarnings("unchecked")
    private void enableValidation(MessageContext context, Unmarshaller u, JAXBContext jc)
    {
        // chack if we have cached schema instance
        Schema validationSchema = (Schema) context.getService()
                .getProperty(GENERATED_VALIDATION_SCHEMA);
        if (validationSchema != null)
        {
            u.setSchema(validationSchema);
            return;
        }
        // Do we have schema dedicated to validation
        Collection<String> schemas = (Collection<String>) context
                .getContextualProperty(VALIDATION_SCHEMA);
        if (schemas == null)
        {
            // No, we don't, so use schema specifed on service
            schemas = (Collection<String>) context.getService()
                    .getProperty(ObjectServiceFactory.SCHEMAS);
        }
        try
        {
            if (schemas != null)
            {
                // We have some schema loaded,so set them up on unmarshaler
                setupValidationSchema(schemas, u);
            }
            else
            {
                final List<DOMResult> results = new ArrayList<DOMResult>();

                jc.generateSchema(new SchemaOutputResolver()
                {

                    public Result createOutput(String ns, String file)
                        throws IOException
                    {
                        DOMResult result = new DOMResult();
                        result.setSystemId(file);
                        results.add(result);
                        return result;
                    }
                });

                DOMSource source = new DOMSource();
                source.setNode(results.get(0).getNode().getFirstChild());
                SchemaFactory factory = SchemaFactory
                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                u.setSchema(factory.newSchema(source));
            }
            // Put generated schema on context
            context.getService().setProperty(GENERATED_VALIDATION_SCHEMA, u.getSchema());

        }
        catch (IOException e)
        {
            // we have configuration problem, so break to application
            throw new XFireRuntimeException("Error creating validating schema.", e);
        }
        catch (SAXException e)
        {
            throw new XFireRuntimeException("Error creating validating schema.", e);
        }

    }
     
    @SuppressWarnings("unchecked")
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            JAXBContext jc = getJAXBContext(context);
            Unmarshaller u = jc.createUnmarshaller();
            u.setAttachmentUnmarshaller(new AttachmentUnmarshaller(context));
            
            // check if validation is enabled
            boolean validationEnabled = Boolean.valueOf((String) context.getContextualProperty(ENABLE_VALIDATION));
            if( validationEnabled){
            	enableValidation(context, u,  jc);
            }
            
            
            Object o;
            if (isAbstract() && reader.getAttributeReader(XSI_TYPE).getValue() == null)
                o = u.unmarshal(reader.getXMLStreamReader(), getTypeClass());
            else
                o = u.unmarshal(reader.getXMLStreamReader());
            
            if (o instanceof JAXBElement)
            {
                o = ((JAXBElement) o).getValue();
            }
            
            return o;
        }
        catch (JAXBException e)
        {
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.RECEIVER);
        }

    }

    @SuppressWarnings("unchecked")
	public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            JAXBContext jc = getJAXBContext(context);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setAttachmentMarshaller(new AttachmentMarshaller(context));

            if (isAbstract())
            {
                MessagePartInfo part = (MessagePartInfo) 
                    context.getProperty(AegisBindingProvider.CURRENT_MESSAGE_PART);
                object = new JAXBElement(part.getName(), getTypeClass(), object);
            }
            
            XMLStreamWriter xsw = ((ElementWriter) writer).getXMLStreamWriter();
            OutputStream os = (OutputStream) context.getOutMessage().getProperty(Channel.OUTPUTSTREAM);
            if (os != null && !(xsw instanceof DOMStreamWriter))
            {
                xsw.writeCharacters("");
                xsw.flush();
                m.setProperty(Marshaller.JAXB_ENCODING, context.getOutMessage().getEncoding());
                m.marshal(object, os);
            }
            else
            {
                m.marshal(object, ((ElementWriter) writer).getXMLStreamWriter());
            }
        }
        catch (JAXBException e)
        {
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.RECEIVER);
        }
        catch (XMLStreamException e)
        {
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.RECEIVER);
        }
    }

    @SuppressWarnings("unchecked")
    public JAXBContext getJAXBContext(MessageContext mc)
        throws JAXBException
    {
        if (context == null)
        {
            String pckg = getTypeClass().getName();
            int i = pckg.lastIndexOf(".");
            if (i != -1)
                pckg = pckg.substring(0, i);
            
            Collection extraPackages = 
                (Collection) mc.getContextualProperty(SEARCH_PACKAGES);
            
            if (extraPackages != null)
            {
                Set<String> pkgSet = new HashSet<String>();
                pkgSet.addAll(extraPackages);
                pkgSet.add(pckg);
                
                StringBuilder pckgs = new StringBuilder();
                boolean first = true;
                
                for (String p : pkgSet)
                {
                    if (!first) pckgs.append(":");
                    else first = false;
                    
                    pckgs.append(p);
                }
                pckg = pckgs.toString();
            }
            context = JAXBContext.newInstance(pckg);
        }
        return context;
    }

    @Override
    public boolean isComplex()
    {
        return true;
    }

    @Override
    public boolean isWriteOuter()
    {
        return false;
    }

    @Override
    public QName getSchemaType()
    {
        return super.getSchemaType();
    }
    
    @SuppressWarnings("unchecked")
    public void initType()
    {
        Class clazz = getTypeClass();
        XmlRootElement root = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
        XmlType type = (XmlType) clazz.getAnnotation(XmlType.class);
        String local = null;
        String nsUri = null;
        
        if (root != null)
        {
            setAbstract(false);
            local = root.name();
            nsUri = root.namespace();
        }
        else if (type != null)
        {
            setAbstract(true);
            local = type.name();
            nsUri = type.namespace();
        }
        else if (clazz.isAnnotationPresent(XmlEnum.class))
        {
            setAbstract(true);
            local = clazz.getSimpleName();
            nsUri = "##default";
        }
        else
        {
            throw new XFireRuntimeException("Couldn't determine element name.");
        }
        
        if (local.equals("##default"))
        {
            local = clazz.getSimpleName();
        }
        
        if (nsUri.equals("##default"))
        {
            nsUri = getPackageNs(clazz);
        }

        setSchemaType(new QName(nsUri, local));
    }

    public static String getPackageNs(Class clazz)
    {
        AnnotatedElement pack =  clazz.getPackage();
        //getPackage isn't guaranteed to return a package
        if (pack == null) 
        {
            try
            {
                pack = Class.forName(clazz.getName().substring(0, clazz.getName().lastIndexOf('.'))
                        + ".package-info", false, clazz.getClassLoader());
            }
            catch (Exception ex)
            {
            }
        }
        
        if (pack == null)
        {
            throw new XFireRuntimeException("No package info found for class " + clazz.getName()
                    + ". Cannot lookup default schema namespace");
        }
        
        XmlSchema schema = pack.getAnnotation(XmlSchema.class);
        String namespace = null;
        if (schema != null)
        {
            namespace = schema.namespace();
        }
        else
        {
            namespace = "";
        }
        return namespace;
    }

    /**
     * JAXB doesn't retain all the schema information at runtime, so schemas
     * must be added manually. So, this method does absolutely nothing.
     */
    @Override
    public void writeSchema(Element root)
    {
    }
}
