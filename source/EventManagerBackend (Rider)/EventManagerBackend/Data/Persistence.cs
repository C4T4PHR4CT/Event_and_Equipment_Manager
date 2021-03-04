using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using EventManagerBackend.Model;

namespace EventManagerBackend.Data
{
    public class Persistence : IPersistence
    {
        private List<Author> _data;

        public Persistence()
        {
            _data = new List<Author>();
            try
            {
                string jsonRead;
                using (StreamReader file = new StreamReader("data.json"))
                    jsonRead = file.ReadToEnd();
                _data = (List<Author>)JsonSerializer.Deserialize(jsonRead, typeof(List<Author>));
            }
            catch {/*ignored*/}
        }
        
        public void PostAuthor(Author author)
        {
            author.Id = _data.Count + 1;
            if (author.FirstName == null)
                throw new Exception("first name can't be null");
            if (author.FirstName.Length > 15)
                throw new Exception("first name can't be longer then 15 characters");
            if (author.LastName == null)
                throw new Exception("last name can't be null");
            if (author.LastName.Length > 15)
                throw new Exception("last name can't be longer then 15 characters");
            _data.Add(author);
            WriteFile();
        }

        public List<Author> GetAuthors()
        {
            return _data;
        }

        public void PostBook(int authorId, Book book)
        {
            foreach (var author in _data)
                foreach (var _book in author.Books)
                    if (book.ISBN == _book.ISBN)
                        throw new Exception("book already exists");
            foreach (var author in _data)
                if (author.Id == authorId)
                {
                    author.Books.Add(book);
                    WriteFile();
                    return;
                }
            throw new Exception("author not found");
        }

        public List<Book> GetBooks()
        {
            List<Book> temp = new List<Book>();
            foreach (var author in _data)
                foreach (var book in author.Books)
                    temp.Add(book);
            return temp;
        }

        public void DeleteBook(int isbn)
        {
            foreach (var author in _data)
                foreach (var book in author.Books)
                    if (book.ISBN == isbn)
                    {
                        author.Books.Remove(book);
                        WriteFile();
                        return;
                    }
            throw new Exception("book not found");
        }

        private void WriteFile()
        {
            string json = JsonSerializer.Serialize(_data, new JsonSerializerOptions {WriteIndented = true});
            using (StreamWriter file = new StreamWriter("data.json"))
                file.WriteLine(json);
        }
    }
}