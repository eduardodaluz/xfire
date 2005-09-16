package org.codehaus.xfire.jaxb;

import javanet.staxutils.ContentHandlerToXMLStreamWriter;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

/**
 * XMLStreamReader to StaxResult converter that ignores startDocument and endDocument
 */
public class FilteringStaxResult extends SAXResult
{

    /**
     * Create a new {@link javax.xml.transform.Result} that produces
     * a result on the specified {@link javax.xml.stream.XMLStreamWriter}
     *
     * @param writer the XMLStreamWriter
     * @throws IllegalArgumentException iff the writer is null
     */
    public FilteringStaxResult(XMLStreamWriter writer)
    {
        if (writer == null)
        {
            throw new IllegalArgumentException();
        }

        super.setHandler(new FilteringContentHandlerToXMLStreamWriter(writer));
    }

    class FilteringContentHandlerToXMLStreamWriter extends ContentHandlerToXMLStreamWriter
    {
        public FilteringContentHandlerToXMLStreamWriter(XMLStreamWriter xmlStreamWriter)
        {
            super(xmlStreamWriter);
        }

        public void startDocument() throws SAXException
        {
        }

        public void endDocument() throws SAXException
        {
        }
    }
}


