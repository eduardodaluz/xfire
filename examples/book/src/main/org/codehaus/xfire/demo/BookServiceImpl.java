package org.codehaus.xfire.demo;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 */
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
    
    public Book findBook(String isbn)
    {
        if (isbn.equals(onlyBook.getIsbn()))
            return onlyBook;
        
        return null;
    }
}
