package org.codehaus.xfire.demo;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 */
public interface BookService
{
    public Book[] getBooks();
    
    public Book findBook(String isbn);
}
