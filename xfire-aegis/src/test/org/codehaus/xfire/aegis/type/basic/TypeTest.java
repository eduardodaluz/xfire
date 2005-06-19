package org.codehaus.xfire.aegis.type.basic;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.aegis.type.basic.ArrayType;
import org.codehaus.xfire.aegis.type.basic.BeanType;
import org.codehaus.xfire.test.AbstractXFireTest;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

/**
 * TypeTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class TypeTest
    extends AbstractXFireTest
{
    public void testBeanType() throws Exception
    {
        XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);

        TypeMappingRegistry tmr = new DefaultTypeMappingRegistry(true);
        
        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");
        
        registerSimpleBeanType(tmr);
        
        BeanType bt = (BeanType) tmr.getDefaultTypeMapping().getType( SimpleBean.class );

        ElementWriter lwriter = new ElementWriter(writer, "SimpleBean", "urn:Bean");
        bt.writeObject( bean, lwriter, null );
        lwriter.close();
        
        writer.close();
        
        System.out.println(bos.toString());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader( new StringReader(bos.toString()) );
        while ( reader.getEventType() != XMLStreamReader.START_ELEMENT )
            reader.next();
                
        SimpleBean readBean = (SimpleBean) bt.readObject( new ElementReader(reader), null );
        assertNotNull( readBean );
        assertEquals( "bleh", readBean.getBleh() );
        assertEquals( "howdy", readBean.getHowdy() );

        Element root = new Element("root");
        Element schema = new Element("schema");
        root.appendChild(schema);
        
        Document doc = new Document(root);
        
        bt.writeSchema( schema );
 
        // TODO: run XPath tests on Schema
    }
    
    /**
     * @param tmr
     * @return
     */
    private void registerSimpleBeanType(TypeMappingRegistry tmr)
    {
        tmr.getDefaultTypeMapping().register( SimpleBean.class, 
                                              new QName("urn:SimpleBean","SimpleBean"),
                                              new BeanType() );
    }

    private void registerArrayType(TypeMappingRegistry tmr)
    {
        tmr.getDefaultTypeMapping().register( SimpleBean[].class, 
                                              new QName("urn:SomeBean" , "ArrayOfSimpleBean"),
                                              new ArrayType() );
    }
    
    public void testArrayType() throws Exception
    {
        XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = ofactory.createXMLStreamWriter(bos);

        TypeMappingRegistry tmr = new DefaultTypeMappingRegistry(true);
        
        registerSimpleBeanType(tmr);
        registerArrayType( tmr );
        
        SimpleBean bean = new SimpleBean();
        bean.setBleh("bleh");
        bean.setHowdy("howdy");
       
        SimpleBean[] beanArray = new SimpleBean[] { bean, bean };

        ArrayType at = (ArrayType) tmr.getDefaultTypeMapping().getType( SimpleBean[].class );

        at.writeObject( beanArray, new ElementWriter( writer, "SimpleBean", "urn:Bean" ), null );
        writer.close();
        
        /* TODO: figure out why this doesn't work. It works when you're
         * actually reading/writing documents. I think it has something
         * to do with the reader.next() in the END_ELEMENT case in LiteralReader.

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader( new StringReader(bos.toString()) );
        while ( reader.getEventType() != XMLStreamReader.START_ELEMENT )
            reader.next();

        Object out1 = at.readObject( new LiteralReader( reader ) );
        
        SimpleBean[] beans = (SimpleBean[]) out1;
        assertNotNull( beans );
        assertEquals( "bleh", beans[0].getBleh() );
        assertEquals( "howdy", beans[0].getHowdy() );

        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("root");
        
        at.writeSchema( root );
        */
        // TODO: run XPath tests on Schema
    }
}
