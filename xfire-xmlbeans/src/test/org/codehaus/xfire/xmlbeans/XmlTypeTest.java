package org.codehaus.xfire.xmlbeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;
import net.webservicex.WeatherData;

import org.codehaus.xfire.aegis.type.DefaultTypeCreator;

public class XmlTypeTest
    extends TestCase
{
    public void testExtraction() throws Exception
    {
        XmlBeansType type = new XmlBeansType(WeatherData.class);
        
        
    }
    
    private void copy(final InputStream input,
                      final OutputStream output,
                      final int bufferSize)
         throws IOException
     {
         try
         {
             final byte[] buffer = new byte[bufferSize];

             int n = 0;
             while (-1 != (n = input.read(buffer)))
             {
                 output.write(buffer, 0, n);
             }
         }
         finally
         {
             input.close();
         }
     }
    
    public void testTypeCreator() throws Exception
    {
        XmlBeansTypeCreator typeCreator = new XmlBeansTypeCreator(new DefaultTypeCreator());
        
        XmlBeansType type = (XmlBeansType) typeCreator.createType(WeatherData.class);
     
        assertNotNull(type);
    }
}
