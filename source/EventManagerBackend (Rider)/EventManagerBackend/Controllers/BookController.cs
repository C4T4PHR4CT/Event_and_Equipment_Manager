using System;
using System.Collections.Generic;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    [Route("book")]
    public class BookController : ControllerBase
    {
        private readonly IPersistence _persistence;
        
        public BookController(IPersistence persistence)
        {
            _persistence = persistence;
        }

        [HttpPost("{authorId}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public IActionResult PostBook([FromRoute] int authorId, [FromBody] Book book)
        {
            try
            {
                //_persistence.PostBook(authorId, book);
                return StatusCode(200);
            }
            catch (Exception e)
            {
                switch (e.Message)
                {
                    case "book already exists":
                        return StatusCode(409);
                    case "author not found":
                        return StatusCode(404);
                    default:
                        return StatusCode(500);
                }
            }
        }
        
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public List<Book> GetBooks()
        {
            return null; //_persistence.GetBooks();
        }
        
        [HttpDelete("{isbn}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public IActionResult DeleteBook([FromRoute] int isbn)
        {
            try
            {
                //_persistence.DeleteBook(isbn);
                return StatusCode(200);
            }
            catch (Exception e)
            {
                switch (e.Message)
                {
                    case "book not found":
                        return StatusCode(404);
                    default:
                        return StatusCode(500);
                }
            }
        }
    }
}