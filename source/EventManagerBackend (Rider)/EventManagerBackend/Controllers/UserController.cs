using System;
using System.Text;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using EventManagerBackend.Model.Exception;
using Microsoft.AspNetCore.Mvc;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IPersistenceService _persistence;
        
        public UserController(IPersistenceService persistence)
        {
            _persistence = persistence;
        }
        
        [HttpGet]
        [Route("token")]
        public IActionResult GetToken([FromHeader(Name = "Authorization")] string basicAuth)
        {
            try
            {
                string user,password;
                try {
                    var temp = basicAuth.Split(" ");
                    if (temp[0] != "Basic")
                        throw new Exception();
                    temp = Encoding.UTF8.GetString(Convert.FromBase64String(temp[1])).Split(":");
                    user = temp[0];
                    password = temp[1];
                } catch (Exception) {
                    throw new UnauthorizedException("Basic authentication format expected!");
                }
                if (_persistence.CheckUserPassword(user, password)) {
                    return StatusCode(200, "token");
                }
                throw new UnauthorizedException("Incorrect credentials!");
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpGet]
        [Route("user/{user}")]
        public IActionResult GetUser([FromRoute] string user)
        {
            try
            {
                User temp = _persistence.GetUser(user);
                return StatusCode(200, temp);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
    }
}