package org.codehaus.xfire.demo.book;

// START SNIPPET: BookService

import java.util.HashMap;
import java.util.Map;

public class BookService
{
	public Map FindBook(Map mapContext)
    {
        String isbn = (String) mapContext.get("isbn");
        
        HashMap results = new HashMap();
        results.put("author", "Dan Diephouse");
        results.put("title", "Boring Biography");
        results.put("isbn", isbn);
        
        return results;
    }
    
    public Map Search(Map mapContext)
    {
        String isbn = (String) mapContext.get("keyword");

        HashMap book = new HashMap();
        book.put("author", "Dan Diephouse");
        book.put("title", "Boring Biography");
        book.put("isbn", isbn);
        
        Map[] books = { book };
        
        HashMap results = new HashMap();
        results.put("books", books);
        
        return results;
    }
}

// END SNIPPET: BookService