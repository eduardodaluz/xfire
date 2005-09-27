package org.codehaus.xfire.jaxb;

import javanet.staxutils.StAXSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;

public class JaxbType extends Type
{
    private QName mySchemaType;

    public JaxbType(Class clazz, JAXBContext jaxbContext)
    {
        mySchemaType = new JaxbIntrospector(jaxbContext).introspect(clazz);
        try
        {
            unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setValidating(false);
        } catch (JAXBException e)
        {
            throw new XFireRuntimeException("error creating unmarshaller", e);
        }
        try
        {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e)
        {
            throw new XFireRuntimeException("error creating marshaller", e);
        }
    }

    Unmarshaller unmarshaller;
    Marshaller marshaller;

    public boolean isAbstract()
    {
        // right now we dont support abstract types
        return false;
    }

    public boolean isComplex()
    {
        return true;
    }

    public Object readObject(MessageReader reader, MessageContext context) throws XFireFault
    {
        try
        {
            XMLStreamReader xmlStreamReader = ((ElementReader) reader).getXMLStreamReader();
            System.out.println(xmlStreamReader.hasNext());
            StAXSource stAXSource = new StAXSource(xmlStreamReader);
            System.out.println(stAXSource.toString());
            return unmarshaller.unmarshal(stAXSource);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.SENDER);
        }
    }

    public void writeObject(Object object, MessageWriter writer, MessageContext context) throws XFireFault
    {
        try
        {

            marshaller.marshal(object, new FilteringStaxResult(((ElementWriter) writer).getXMLStreamWriter()));
        } catch (JAXBException e)
        {
            throw new XFireFault("Could not marshall type.", e, XFireFault.RECEIVER);
        }

    }

    public QName getSchemaType()
    {
        return mySchemaType;
    }

    public boolean isWriteOuter()
    {
        return false;
    }
}
