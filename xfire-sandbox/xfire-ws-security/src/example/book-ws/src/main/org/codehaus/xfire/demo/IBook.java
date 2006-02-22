package org.codehaus.xfire.demo;

public interface IBook
{
    Book[] getBooks();
    
    
    Book findBook(String isbn);
    
}
