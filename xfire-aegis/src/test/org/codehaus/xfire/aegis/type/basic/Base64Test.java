package org.codehaus.xfire.aegis.type.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.aegis.stax.ElementReader;
import org.codehaus.xfire.aegis.stax.ElementWriter;
import org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.test.AbstractXFireTest;

public class Base64Test
    extends AbstractXFireTest
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
        Base64Type type = new Base64Type();
        
        byte[] data = new byte[10];
        
        File file = File.createTempFile("base64", "xml");
        FileOutputStream fos = new FileOutputStream(file);
        ElementWriter writer = new ElementWriter(fos, "base64", "urn:test");
        type.writeObject(data, 
                         writer, 
                         new MessageContext());
        writer.close();
        writer.flush();
        fos.close();
        
        FileInputStream fis = new FileInputStream(file);
        byte[] data2 = (byte[]) type.readObject(new ElementReader(fis), new MessageContext());
        
        assertEquals(data.length, data2.length);
        
        fis.close();
        file.deleteOnExit();
    }
    
}
