package org.codehaus.xfire.demo;

import java.net.MalformedURLException;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

/**
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 */
public class BookClient
{
    public static void main(String args[])
    {
        String serviceURL = "http://localhost:8080/book/services/BookService";
        Service serviceModel = new ObjectServiceFactory().create(BookService.class);
        XFireProxyFactory serviceFactory = new XFireProxyFactory();
        
        try
        {
            BookService service = (BookService) serviceFactory.create(serviceModel, serviceURL);
            
            Book[] books = service.getBooks();
            
            System.out.println("BOOKS:");
            
            for (int i = 0; i < books.length; i++)
            {
                System.out.println(books[i].getTitle());
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
}
