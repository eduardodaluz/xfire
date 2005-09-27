package org.codehaus.xfire.jaxb2;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.yom.Element;

public class JaxbType
    extends Type
{
    private JAXBContext context;
    
    public JaxbType(Class clazz)
    {
        setTypeClass(clazz);
    }

    public Object readObject(MessageReader reader, MessageContext context)
        throws XFireFault
    {
        try
        {
            JAXBContext jc = getJAXBContext();

            Unmarshaller u = jc.createUnmarshaller();
            return u.unmarshal( ((ElementReader) reader).getXMLStreamReader() );
        }
        catch (JAXBException e)
        {
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.RECEIVER);
        }
        
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context)
        throws XFireFault
    {
        try
        {
            JAXBContext jc = getJAXBContext();
            
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.marshal( object, ((ElementWriter) writer).getXMLStreamWriter() );
        }
        catch (JAXBException e)
        {
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.RECEIVER);
        }
    }

    public JAXBContext getJAXBContext() throws JAXBException
    {
        if (context == null)
        {
            context = JAXBContext.newInstance( getTypeClass() );
        }
        return context;
    }

    @Override
    public boolean isAbstract()
    {
        return false;
    }

    @Override
    public QName getSchemaType()
    {
        return getSchemaType(getTypeClass());
    }

    public static QName getSchemaType(Class clazz)
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance( clazz);

            QName name =  jc.createJAXBIntrospector().getElementName(clazz.newInstance());

            return name;
        }
        catch (Exception e)
        {
            throw new XFireRuntimeException("Couldn't determine element name.", e);
        }
    }

    /**
     * JAXB doesn't retain all the schema information at runtime, so schemas must be
     * added manually. So, this method does absolutely nothing.
     */
    @Override
    public void writeSchema(Element root)
    {
    }
}
