package org.codehaus.xfire.aegis.type.basic;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.xfire.util.STAXUtils;
import org.jdom.Document;
import org.jdom.Element;

/**
 * TypeTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TypeTest
    extends AbstractXFireTest
{
    public void testBeanType()
        throws Exception
    {
        // XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(bos, null, null);
        TypeMappingRegistry tmr = new DefaultTypeMappingRegistry(true);
        TypeMapping tm = tmr.createTypeMapping(true);

        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");

        registerSimpleBeanType(tm);

        BeanType bt = (BeanType) tm.getType(SimpleBean.class);

        ElementWriter lwriter = new ElementWriter(writer, "SimpleBean", "urn:Bean");
        bt.writeObject(bean, lwriter, new MessageContext());
        lwriter.close();

        writer.close();

        System.out.println(bos.toString());
        // XMLInputFactory factory = XMLInputFactory.newInstance();
        // XMLStreamReader reader = factory.createXMLStreamReader( new
        // StringReader(bos.toString()) );
        XMLStreamReader reader = STAXUtils.createXMLStreamReader(new StringReader(bos.toString()));
        while (reader.getEventType() != XMLStreamReader.START_ELEMENT)
            reader.next();

        SimpleBean readBean = (SimpleBean) bt.readObject(new ElementReader(reader), new MessageContext());
        assertNotNull(readBean);
        assertEquals("bleh", readBean.getBleh());
        assertEquals("howdy", readBean.getHowdy());

        Element root = new Element("root");
        Element schema = new Element("schema");
        root.addContent(schema);

        Document doc = new Document(root);

        bt.writeSchema(schema);

        // TODO: run XPath tests on Schema
    }

    /**
     * @param tmr
     * @return
     */
    private void registerSimpleBeanType(TypeMapping tmr)
    {
        tmr.register(SimpleBean.class, new QName("urn:SimpleBean", "SimpleBean"), new BeanType());
    }

    private void registerArrayType(TypeMapping tmr)
    {
        tmr.register(SimpleBean[].class,
                     new QName("urn:SomeBean", "ArrayOfSimpleBean"),
                     new ArrayType());
    }

    public void testArrayType()
        throws Exception
    {
        // XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);
        XMLStreamWriter writer = STAXUtils.createXMLStreamWriter(bos, null, null);
        TypeMappingRegistry tmr = new DefaultTypeMappingRegistry(true);
        TypeMapping tm = tmr.createTypeMapping(true);

        registerSimpleBeanType(tm);
        registerArrayType(tm);

        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");

        SimpleBean[] beanArray = new SimpleBean[] { bean, bean };

        ArrayType at = (ArrayType) tm.getType(SimpleBean[].class);

        at.writeObject(beanArray,
                       new ElementWriter(writer, "SimpleBean", "urn:Bean"),
                       new MessageContext());
        writer.close();

        /*
         * TODO: figure out why this doesn't work. It works when you're actually
         * reading/writing documents. I think it has something to do with the
         * reader.next() in the END_ELEMENT case in LiteralReader.
         * 
         * XMLInputFactory factory = XMLInputFactory.newInstance();
         * XMLStreamReader reader = factory.createXMLStreamReader( new
         * StringReader(bos.toString()) ); while ( reader.getEventType() !=
         * XMLStreamReader.START_ELEMENT ) reader.next();
         * 
         * Object out1 = at.readObject( new LiteralReader( reader ) );
         * 
         * SimpleBean[] beans = (SimpleBean[]) out1; assertNotNull( beans );
         * assertEquals( "bleh", beans[0].getBleh() ); assertEquals( "howdy",
         * beans[0].getHowdy() );
         * 
         * Document doc = DocumentHelper.createDocument(); Element root =
         * doc.addElement("root");
         * 
         * at.writeSchema( root );
         */
        // TODO: run XPath tests on Schema
    }
}
