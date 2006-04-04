package org.codehaus.xfire.castor;

public class BookService
{
    private Book onlyBook;

    public BookService()
    {
        onlyBook = new Book();
        onlyBook.setAuthor("Steve Ballmer");
        onlyBook.setTitle("How to Yell Real Loud and Look Like You Might Have a Heart Attack");
        onlyBook.setIsbn("012924828");
    }

    public String addBook(Book book)
    {
        return onlyBook.getIsbn();
    }

    public Book findBook(String isbn)
    {
        return onlyBook;
    }
}
