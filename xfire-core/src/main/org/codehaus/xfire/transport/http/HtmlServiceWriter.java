package org.codehaus.xfire.transport.http;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.service.Service;

/**
 * Provides a basic HTML description of a {@link Service}.
 *
 * @author <a href="poutsma@mac.com">Arjen Poutsma</a>
 */
public class HtmlServiceWriter
{
    private static final String XHTML_STRICT_DTD = "<!DOCTYPE html " +
            "PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

    /**
     * Writes a HTML list of services to the given stream. Each service is described with its name.
     *
     * @param out      the stream to write to
     * @param services the services
     * @throws XMLStreamException if an XML writing exception occurs
     */
    public void write(OutputStream out, Collection services)
            throws XMLStreamException
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);

        writer.writeStartDocument();
        writePreamble(writer, "XFire Services");

        writer.writeStartElement("body");
        writer.writeStartElement("p");
        writer.writeCharacters("No such service");
        writer.writeEndElement(); // p
        if (!services.isEmpty())
        {
            writer.writeStartElement("p");
            writer.writeCharacters("Services:");
            writer.writeEndElement(); // p
            writer.writeStartElement("ul");
            for (Iterator iterator = services.iterator(); iterator.hasNext();)
            {
                Service service = (Service) iterator.next();
                writer.writeStartElement("li");
                writer.writeCharacters(service.getName());
                writer.writeEndElement(); // li
            }
        }

        writer.writeEndDocument();
        writer.flush();
    }

    /**
     * Writes a HTML description of a service to the given stream.
     *
     * @param out     the stream to write to
     * @param service the service
     * @throws XMLStreamException if an XML writing exception occurs
     */
    public void write(OutputStream out, Service service)
            throws XMLStreamException
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out);

        writer.writeStartDocument();
        String title = service.getName() + " Web Service";
        writePreamble(writer, title);

        writer.writeStartElement("body");
        writer.writeStartElement("h1");
        writer.writeCharacters(title);
        writer.writeEndElement(); // h1

        writer.writeEndDocument();
        writer.flush();
    }

    private void writePreamble(XMLStreamWriter writer, String title)
            throws XMLStreamException
    {
        writer.writeDTD(XHTML_STRICT_DTD);
        writer.writeStartElement("html");
        writer.writeStartElement("head");
        writer.writeStartElement("title");
        writer.writeCharacters(title);
        writer.writeEndElement(); // title
        writer.writeEndElement(); // head
    }


}
