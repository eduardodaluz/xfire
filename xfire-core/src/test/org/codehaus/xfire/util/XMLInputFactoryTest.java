package org.codehaus.xfire.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.test.AbstractXFireTest;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 *  @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public class XMLInputFactoryTest
    extends AbstractXFireTest
{

    public void testFactoryConfig()
        throws Exception
    {

        String xml = "<root><foo><![CDATA[data]]></foo></root>";

        MessageContext ctx = new MessageContext();
        ctx.setProperty(XFire.STAX_INPUT_FACTORY, WstxInputFactory.class.getName());
        ctx.setProperty(XMLInputFactory.IS_COALESCING, "false");
        XMLStreamReader xmlReader = STAXUtils.createXMLStreamReader(new StringReader(xml), ctx);
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        XMLStreamWriter xmlWriter = STAXUtils.createXMLStreamWriter(outStream, null, null);
        STAXUtils.copy(xmlReader, xmlWriter);
        xmlWriter.close();
        xmlReader.close();
        String result = outStream.toString();
        assertTrue(result.indexOf("CDATA") > 0);

    }

}
