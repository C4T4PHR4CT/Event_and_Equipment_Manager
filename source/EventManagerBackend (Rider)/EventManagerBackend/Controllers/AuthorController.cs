using System.Collections.Generic;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    [Route("author")]
    public class AuthorController : ControllerBase
    {
        private readonly IPersistence _persistence;
        
        public AuthorController(IPersistence persistence)
        {
            _persistence = persistence;
        }

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public IActionResult PostAuthor([FromBody] Author author)
        {
            author.Books = new List<Book>();
            try
            {
                //_persistence.PostAuthor(author);
                return StatusCode(200);
            }
            catch
            {
                return StatusCode(400);
            }
        }
        
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public List<Author> GetAuthors()
        {
            return null; //_persistence.GetAuthors();
        }
    }
}