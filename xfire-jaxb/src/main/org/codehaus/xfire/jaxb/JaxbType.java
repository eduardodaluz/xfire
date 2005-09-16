package org.codehaus.xfire.jaxb;

import javanet.staxutils.StAXSource;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.fault.XFireFault;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

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
            Object obj = unmarshaller.unmarshal(stAXSource);
            return obj;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new XFireFault("Could not unmarshall type.", e, XFireFault.SENDER);
        }
    }

    private static void dumpS(XMLStreamReader parser)
    {


        try
        {


            int inHeader = 0;
            for (int event = parser.next();
                 event != XMLStreamConstants.END_DOCUMENT;
                 event = parser.next())
            {
                switch (event)
                {
                    case XMLStreamConstants.START_ELEMENT:
                        System.out.println(parser.getLocalName());
                        inHeader++;
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        inHeader--;
                        if (inHeader == 0) System.out.println();
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (inHeader > 0) System.out.print(parser.getText());
                        break;
                    case XMLStreamConstants.CDATA:
                        if (inHeader > 0) System.out.print(parser.getText());
                        break;
                } // end switch
            } // end while
            parser.close();
        }
        catch (XMLStreamException ex)
        {
            System.out.println(ex);
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
}
