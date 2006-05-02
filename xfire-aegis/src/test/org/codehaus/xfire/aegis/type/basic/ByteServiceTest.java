package org.codehaus.xfire.aegis.type.basic;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.xfire.aegis.AbstractXFireAegisTest;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;

public class ByteServiceTest extends AbstractXFireAegisTest
{
    public void testService() throws Exception {
        Service service = getServiceFactory().create(ByteService.class);
        service.setInvoker(new BeanInvoker(new ByteService() {
            public byte[] echo(byte[] bytes)
            {
                return bytes;
            }
        }));
        getServiceRegistry().register(service);
        
        ByteService client = (ByteService) new XFireProxyFactory(getXFire()).create(service, "xfire.local://ByteService");
        
        byte[] data1 = read("pom.xml");
        byte[] data2 = client.echo(data1);
        assertEquals(data1.length, data2.length);
    }
    
    private byte[] read(String string) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream stream = new FileInputStream(getTestFile(string));
        
        copy(stream, bos, 8096);
        
        return bos.toByteArray();
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
    
    public static interface ByteService {
        public byte[] echo(byte[] bytes);
    }
}