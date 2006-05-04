package org.codehaus.xfire.jaxb2;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.transport.Channel;
import org.jdom.Element;

public class JaxbType
    extends Type
{
    private JAXBContext context;

    public JaxbType(Class clazz)
    {
        setTypeClass(clazz);

        initType();
    }

    @SuppressWarnings("unchecked")
    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            JAXBContext jc = getJAXBContext();

            Unmarshaller u = jc.createUnmarshaller();
            u.setAttachmentUnmarshaller(new AttachmentUnmarshaller(context));
            if (isAbstract()) 
            {
                 JAXBElement element = u.unmarshal(reader.getXMLStreamReader(), getTypeClass());
                 return element.getValue();
            }
            else
            {
                return u.unmarshal(reader.getXMLStreamReader());
            }
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
            JAXBContext jc = getJAXBContext();

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setAttachmentMarshaller(new AttachmentMarshaller(context));

            if (isAbstract())
            {
                MessagePartInfo part = (MessagePartInfo) 
                    context.getProperty(AegisBindingProvider.CURRENT_MESSAGE_PART);
                object = new JAXBElement(part.getName(), getTypeClass(), object);
            }
            
            OutputStream os = (OutputStream) context.getOutMessage().getProperty(Channel.OUTPUTSTREAM);
            if (os != null)
            {
                XMLStreamWriter xsw = ((ElementWriter) writer).getXMLStreamWriter();
                xsw.writeCharacters("");
                xsw.flush();
                
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

    public JAXBContext getJAXBContext()
        throws JAXBException
    {
        if (context == null)
        {
            context = JAXBContext.newInstance(getTypeClass());
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
        Package pack = clazz.getPackage();
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
