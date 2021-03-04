using System.Collections.Generic;
using EventManagerBackend.Model;

namespace EventManagerBackend.Data
{
    public interface IPersistence
    {
        public void PostAuthor(Author author);
        public List<Author> GetAuthors();
        public void PostBook(int authorId, Book book);
        public List<Book> GetBooks();
        public void DeleteBook(int isbn);
    }
}