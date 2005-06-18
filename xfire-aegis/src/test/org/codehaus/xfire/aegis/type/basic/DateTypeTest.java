package org.codehaus.xfire.aegis.type.basic;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.aegis.yom.YOMWriter;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.yom.Document;
import org.codehaus.yom.Element;

public class DateTypeTest
    extends AbstractXFireAegisTest
{
    TypeMapping mapping;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        addNamespace("t", "urn:test");
        addNamespace("xsd", SoapConstants.XSD);
        addNamespace("xsi", SoapConstants.XSI_NS);
        
        TypeMappingRegistry reg = new DefaultTypeMappingRegistry(true);
        mapping = reg.createTypeMapping(true);
    }

    public void testBean()
        throws Exception
    {
        String ns = "http://basic.type.aegis.xfire.codehaus.org";
        DateType type = new DateType();
        type.setTypeClass(Date.class);
        type.setSchemaType(new QName(ns, "date"));

        mapping.register(type);
        
        Type dtoType = mapping.getTypeCreator().createType(DateDTO.class);
        mapping.register(dtoType);
        
        // Test reading
        ElementReader reader = new ElementReader(getResourceAsStream("/org/codehaus/xfire/aegis/type/basic/dates.xml"));
        
        Object obj = dtoType.readObject(reader, new MessageContext());
        DateDTO dto = (DateDTO) obj;
        assertNotNull(dto.getDate0());
        assertNotNull(dto.getDateTime0());
        assertNotNull(dto.getDateTime1());
        assertNotNull(dto.getDateTime2());
        assertNotNull(dto.getDateTime3());
        assertNotNull(dto.getDateTime4());
        
        assertTrue ( dto.getDateTime3().before( dto.getDateTime4() ) );
        
        assertNotNull(dto.getDateTime5());
        assertNotNull(dto.getDateTime6());
        
        assertTrue ( dto.getDateTime5().before( dto.getDateTime6() ) );
        
        assertNotNull(dto.getTime0());
        assertNotNull(dto.getTime1());
        
        Element element = new Element("dates", ns);
        Document doc = new Document(element);
        YOMWriter writer = new YOMWriter(element);
        dtoType.writeObject(dto, writer, new MessageContext());
        writer.close();
        
        addNamespace("d", ns);
        assertValid("/d:dates/d:date0", element);
        assertValid("/d:dates/d:dateTime0", element);
        assertValid("/d:dates/d:time0", element);
    }
    
    public static class DateDTO
    {
        private Date date0;
        private Date dateTime0;
        private Timestamp dateTime1;
        private Calendar dateTime2;
        private Date dateTime3;
        private Date dateTime4;
        private Date dateTime5;
        private Date dateTime6;
        private Time time0;
        private Time time1;
        
        public Date getDate0()
        {
            return date0;
        }
        public void setDate0(Date date0)
        {
            this.date0 = date0;
        }
        public Date getDateTime0()
        {
            return dateTime0;
        }
        public void setDateTime0(Date dateTime0)
        {
            this.dateTime0 = dateTime0;
        }
        public Timestamp getDateTime1()
        {
            return dateTime1;
        }
        public void setDateTime1(Timestamp dateTime1)
        {
            this.dateTime1 = dateTime1;
        }
        public Calendar getDateTime2()
        {
            return dateTime2;
        }
        public void setDateTime2(Calendar dateTime2)
        {
            this.dateTime2 = dateTime2;
        }
        public Date getDateTime3()
        {
            return dateTime3;
        }
        public void setDateTime3(Date dateTime3)
        {
            this.dateTime3 = dateTime3;
        }
        public Date getDateTime4()
        {
            return dateTime4;
        }
        public void setDateTime4(Date dateTime4)
        {
            this.dateTime4 = dateTime4;
        }
        public Date getDateTime5()
        {
            return dateTime5;
        }
        public void setDateTime5(Date dateTime5)
        {
            this.dateTime5 = dateTime5;
        }
        public Date getDateTime6()
        {
            return dateTime6;
        }
        public void setDateTime6(Date dateTime6)
        {
            this.dateTime6 = dateTime6;
        }
        public Time getTime0()
        {
            return time0;
        }
        public void setTime0(Time time0)
        {
            this.time0 = time0;
        }
        public Time getTime1()
        {
            return time1;
        }
        public void setTime1(Time time1)
        {
            this.time1 = time1;
        }
        
    }
}
