package org.codehaus.xfire.mtom;

import java.net.MalformedURLException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.HttpTransport;

/**
 * <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class MTOMClient
{
    public static void main(String[] args)
        throws MalformedURLException
    {
        Service serviceModel = new ObjectServiceFactory()
                .create(MTOMService.class,
                        "MTOMService",
                        "http://xfire.codehaus.org/MTOMService",
                        null);

        MTOMService service = (MTOMService) new XFireProxyFactory()
                .create(serviceModel, "http://localhost:8080/mtom/services/MTOMService");

         // Setup properties
        Client client = Client.getInstance(service);
        client.setProperty("mtom-enabled", "true");
        client.setProperty(HttpTransport.CHUNKING_ENABLED, "true");
        // Byte array
        String result = service.stringFromBytes("tsztelak@gmail.com".getBytes());
        System.out.print("Result : "+ result+"\n");
        // Data Source
        DataSource source = new ByteArrayDataSource("tsztelak@gmail.com".getBytes(), "text/plain; charset=UTF-8");
        result = service.stringFromDataSource(source);
        System.out.print("Result : "+ result+"\n");
        // Data Handler
        result = service.stringFromDataHandler(new DataHandler(source));
        System.out.print("Result : "+ result+"\n");
        return ;
    }
}
