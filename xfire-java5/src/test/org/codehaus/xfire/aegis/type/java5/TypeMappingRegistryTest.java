package org.codehaus.xfire.aegis.type.java5;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.MessageReader;
import org.codehaus.xfire.aegis.MessageWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.easymock.MockControl;

public class TypeMappingRegistryTest extends TestCase
{
    
    public void testXMLGregorian() throws Exception
    {
        DefaultTypeMappingRegistry registry = new DefaultTypeMappingRegistry();
        registry.createDefaultMappings();
        
        TypeMapping tm = registry.getDefaultTypeMapping();

        Type type = tm.getType(XMLGregorianCalendar.class);
        assertEquals("dateTime", type.getSchemaType().getLocalPart());
        
        MockControl readerControl = MockControl.createControl(MessageReader.class);
        MessageReader reader = (MessageReader) readerControl.getMock();
        
        reader.getValue();
        readerControl.setDefaultReturnValue("---28");
        
        readerControl.replay();
        
        XMLGregorianCalendarType xType = (XMLGregorianCalendarType) tm.getType(XMLGregorianCalendar.class);
        assertNotNull(xType);
        XMLGregorianCalendar cal = (XMLGregorianCalendar) xType.readObject(reader, new MessageContext());
        assertEquals(28, cal.getDay());
        
        readerControl.verify();
        
        // test writing
        MockControl writerControl = MockControl.createControl(MessageWriter.class);
        MessageWriter writer = (MessageWriter) writerControl.getMock();
        
        writer.writeValue("---28");
        writerControl.setVoidCallable();
        
        writerControl.replay();
        
        xType.writeObject(cal, writer, new MessageContext());
        
        writerControl.verify();
    }
    
    public void testDurationType() throws Exception
    {
        DefaultTypeMappingRegistry registry = new DefaultTypeMappingRegistry();
        registry.createDefaultMappings();
        
        TypeMapping tm = registry.getDefaultTypeMapping();
        
        MockControl readerControl = MockControl.createControl(MessageReader.class);
        MessageReader reader = (MessageReader) readerControl.getMock();
        
        reader.getValue();
        readerControl.setDefaultReturnValue("-P120D");
        
        readerControl.replay();
        
        DurationType dType = (DurationType) tm.getType(Duration.class);
        assertNotNull(dType);
        Duration d = (Duration) dType.readObject(reader, new MessageContext());
        assertEquals(120, d.getDays());
        
        readerControl.verify();
        
        // test writing
        MockControl writerControl = MockControl.createControl(MessageWriter.class);
        MessageWriter writer = (MessageWriter) writerControl.getMock();
        
        writer.writeValue("-P120D");
        writerControl.setVoidCallable();
        
        writerControl.replay();
        
        dType.writeObject(d, writer, new MessageContext());
        assertEquals(120, d.getDays());
        
        writerControl.verify();
    }
}
