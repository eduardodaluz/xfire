package org.codehaus.xfire.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 */
//START SNIPPET: book
public class BookServiceImpl implements BookService
{
    private Book onlyBook;
    
    public BookServiceImpl()
    {
        onlyBook = new Book();
        onlyBook.setAuthor("Dan Diephouse");
        onlyBook.setTitle("Using XFire");
        onlyBook.setIsbn("0123456789");
    }

    public Book[] getBooks() 
    {
        return new Book[] { onlyBook };
    }
    
    public Book findBook(String isbn) throws BookException
    {
        if (isbn.equals(onlyBook.getIsbn()))
            return onlyBook;
        
        throw new BookException("Book not exists",new BookExceptionDetail("NOT_EXIST","Can't find book"));
    }

	public Map getBooksMap() {
		Map result = new HashMap();
		result.put(onlyBook.getIsbn(), onlyBook);
		return result;
	}
}
//END SNIPPET: book
