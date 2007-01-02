package org.codehaus.xfire.demo;

import java.util.Map;

/**
 * 
 * Service finding books
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * @author <a href="mailto:nathanyp@hotmail.com">Nathan Peles</a>
 * 
 */
public interface BookService
{
    /**
     * returns array of the books
     * @return all books
     */
    public Book[] getBooks();
    
    /**
     * Find book by id
     * @param isbn isbn number
     * @return found book
     * @throws BookException cos tam
     */
    public Book findBook(String isbn) throws BookException;
    
    
    /**
     * return book map
     * @return all book as map
     */
    public Map getBooksMap();
}
