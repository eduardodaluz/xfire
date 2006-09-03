package org.codehaus.xfire.aegis.stax;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;

import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.jdom.JDOMWriter;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 4, 2004
 */
public class WriterTest
    extends AbstractXFireTest
{
    File output;
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        output = File.createTempFile("writetest", ".xml");
    }

    protected void tearDown()
        throws Exception
    {
        if (output.exists())
            output.delete();
        
        super.tearDown();
    }

    public void testLiteral()
        throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ElementWriter writer = new ElementWriter(bos, "root", "urn:test");

        write(writer);

        writer.flush();
        bos.close();
        
        System.out.println(bos.toString());
        StaxBuilder builder = new StaxBuilder();
        Document doc = builder.build(new StringReader(bos.toString()));
        
        testWrite(doc);
    }
    
    public void testYOM()
        throws Exception
    {
        Document doc = new Document(new Element("root", "urn:test"));
        
        write(new JDOMWriter(doc.getRootElement()));
        
        testWrite(doc);
    }
    
    public void write(MessageWriter writer)
    {
        MessageWriter nons = writer.getElementWriter("nons");
        nons.writeValue("nons");
        nons.close();
        
        MessageWriter intval = writer.getElementWriter("int");
        intval.writeValueAsInt(new Integer(10000));
        intval.close();

        MessageWriter child1 = writer.getElementWriter("child1", "urn:child1");
        MessageWriter att1 = child1.getAttributeWriter("att1");
        att1.writeValue("att1");
        att1.close();
        MessageWriter att2 = child1.getAttributeWriter("att2", "");
        att2.writeValue("att2");
        att2.close();
        MessageWriter att3 = child1.getAttributeWriter("att3", "urn:att3");
        att3.writeValue("att3");
        att3.close();
        MessageWriter att4 = child1.getAttributeWriter("att4", null);
        att4.writeValue("att4");
        att4.close();
        
        child1.close();
        
        writer.close();
    }
    
    public void testWrite(Document doc) throws Exception
    {
        addNamespace("t", "urn:test");
        addNamespace("c", "urn:child1");
        addNamespace("a", "urn:att3");
        
        assertValid("/t:root/t:nons[text()='nons']", doc);
        assertValid("/t:root/t:int[text()='10000']", doc);
        assertValid("/t:root/c:child1", doc);
        assertValid("/t:root/c:child1[@c:att1]", doc);
        assertValid("/t:root/c:child1[@att2]", doc);
        assertValid("/t:root/c:child1[@a:att3]", doc);
        assertValid("/t:root/c:child1[@att4]", doc);
    }
}
