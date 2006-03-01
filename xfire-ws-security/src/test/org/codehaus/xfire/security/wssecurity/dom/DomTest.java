package org.codehaus.xfire.security.wssecurity.dom;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.util.DOMUtils;
import org.codehaus.xfire.util.stax.W3CDOMStreamReader;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class DomTest
    extends TestCase
{
    public void testDOM()
        throws Exception
    {

        InputStream inStream = getClass().getResourceAsStream("dom.xml");
        Document doc = DOMUtils.readXml(inStream);
        inStream.close();

        W3CDOMStreamReader xmlReader = new W3CDOMStreamReader(doc.getDocumentElement());
        /*
         * XMLStreamReader xmlReader =
         * XMLInputFactory.newInstance().createXMLStreamReader(inStream,
         * "UTF-8");
         */
        int type = 0;
        while (xmlReader.hasNext())
        {
            type = xmlReader.next();
            if (type == XMLStreamConstants.START_ELEMENT)
            {
                int nsC = xmlReader.getNamespaceCount();
                int aC = xmlReader.getAttributeCount();
                int z = 0;
                int w = z;
                for(int i=0;i<aC;i++){
                    QName name = xmlReader.getAttributeName(i);
                    System.out.print(" attr : "+name.getLocalPart()+" ,");
                }
            }
            System.out.print(type + "\n");
        }

    }

}
