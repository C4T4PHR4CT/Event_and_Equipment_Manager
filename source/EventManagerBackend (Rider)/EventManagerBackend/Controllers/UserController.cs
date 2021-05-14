using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text.RegularExpressions;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using EventManagerBackend.Model.Exception;
using Microsoft.AspNetCore.Cryptography.KeyDerivation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IPersistenceService _persistence;
        private readonly IConfigService _config;
        
        public UserController(IPersistenceService persistence, IConfigService config)
        {
            _persistence = persistence;
            _config = config;
        }

        [HttpPost]
        [Route("user")]
        public IActionResult PostUser([FromBody] User user)
        {
            try
            {
                if (HttpContext.Items["User"] == null && _config.UserPostPermissionLevel > 0)
                    throw new UnauthorizedException("Authorization failed!");
                int userLevel;
                if (HttpContext.Items["User"] == null)
                    userLevel = 1;
                else
                    userLevel = (int) ((User) HttpContext.Items["User"]).PermissionLevel;
                if (userLevel < _config.UserPostPermissionLevel)
                    throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                //check constraints
                CheckUsername(user.Name);
                CheckPassword(user.Password);
                //hash password
                user.Password = Convert.ToBase64String(KeyDerivation.Pbkdf2(user.Password, _config.Salt, KeyDerivationPrf.HMACSHA1, 1000, 256 / 8));
                switch (userLevel)
                {
                    case 1:
                        user.PermissionLevel = 1;
                        if (HttpContext.Items["User"] != null)
                            if (((User) HttpContext.Items["User"]).OrganizationId != null)
                                user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                            else
                                throw new Exception("Data integrity broken!\ncustomers must have an assigned organization");
                        break;
                    case 2:
                        user.PermissionLevel = 1;
                        if (((User) HttpContext.Items["User"]).OrganizationId != null)
                            user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                        else
                            throw new Exception("Data integrity broken!\nemployees must have an assigned organization");
                        break;
                    case 3:
                        if (user.PermissionLevel != null)
                        {
                            if (user.PermissionLevel >= 3)
                                user.PermissionLevel = 2;
                        }
                        else
                        {
                            int level = _persistence.GetPermissionLevel(user.Permission);
                            if (level >= 3)
                                user.PermissionLevel = 2;
                            else
                                user.PermissionLevel = level;
                        }
                        if (((User) HttpContext.Items["User"]).OrganizationId != null)
                            user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                        else
                            throw new Exception("Data integrity broken!\nemployers must have an assigned organization");
                        break;
                    case 4:
                        if (user.PermissionLevel != null)
                        {
                            if (user.PermissionLevel > 4)
                                user.PermissionLevel = 4;
                        }
                        else
                        {
                            int level = _persistence.GetPermissionLevel(user.Permission);
                            if (level >= 4)
                                user.PermissionLevel = 4;
                            else
                                user.PermissionLevel = level;
                        }
                        if (user.OrganizationId == null && user.Organization != null)
                            user.OrganizationId = _persistence.GetOrganization(user.Organization);
                        break;
                    case 5:
                        if (user.PermissionLevel != null)
                        {
                            if (user.PermissionLevel > 5)
                                user.PermissionLevel = 5;
                        }
                        else
                        {
                            int level = _persistence.GetPermissionLevel(user.Permission);
                            if (level >= 5)
                                user.PermissionLevel = 5;
                            else
                                user.PermissionLevel = level;
                        }
                        if (user.OrganizationId == null && user.Organization != null)
                            user.OrganizationId = _persistence.GetOrganization(user.Organization);
                        break;
                }
                if (HttpContext.Items["User"] != null && user.PermissionLevel <= 3 && user.OrganizationId == null)
                    throw new ConflictException("customers, employees and employers must have an assigned organization");
                _persistence.CreateUser(user);
                return StatusCode(200);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }

        [HttpGet]
        [Route("user")]
        public IActionResult GetAllUsers()
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                if (((User) HttpContext.Items["User"]).PermissionLevel < 2)
                    throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                var temp = _persistence.GetAllUsers(((User) HttpContext.Items["User"]).OrganizationId);
                return StatusCode(200, temp);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }

        [HttpDelete]
        [Route("user/id/{userId}")]
        public IActionResult DeleteOrArchiveUser([FromRoute] int userId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                if (((User) HttpContext.Items["User"]).PermissionLevel < 3)
                {
                    if (((User) HttpContext.Items["User"]).Id != userId)
                        throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                    _persistence.DeleteUser(userId);
                    return StatusCode(200);
                }
                if (((User) HttpContext.Items["User"]).PermissionLevel == 3)
                {
                    var temp = _persistence.GetUserById(userId);
                    if (((User) HttpContext.Items["User"]).PermissionLevel <= temp.PermissionLevel || ((User) HttpContext.Items["User"]).OrganizationId != null && ((User) HttpContext.Items["User"]).OrganizationId != temp.OrganizationId)
                        throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                    _persistence.DeleteUser(userId);
                    return StatusCode(200);
                }
                if (((User) HttpContext.Items["User"]).PermissionLevel == 4)
                {
                    var temp = _persistence.GetUserById(userId);
                    if (((User) HttpContext.Items["User"]).PermissionLevel <= temp.PermissionLevel)
                        throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                    _persistence.DeleteUser(userId);
                    return StatusCode(200);
                }
                if (((User) HttpContext.Items["User"]).PermissionLevel > 4)
                {
                    _persistence.DeleteUser(userId);
                    return StatusCode(200);
                }
                return StatusCode(500);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }

        [HttpPut]
        [Route("user/id/{userId}")]
        public IActionResult PutUser([FromRoute] int userId, [FromBody] User user)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                if (user.Name != null)
                {
                    CheckUsername(user.Name);
                }
                if (user.Password != null)
                {
                    CheckPassword(user.Password);
                    user.Password = Convert.ToBase64String(KeyDerivation.Pbkdf2(user.Password, _config.Salt, KeyDerivationPrf.HMACSHA1, 1000, 256 / 8));
                }
                user.Id = userId;
                var current = _persistence.GetUserById(userId);
                switch (((User) HttpContext.Items["User"]).PermissionLevel)
                {
                    case 1:
                        user.PermissionLevel = 1;
                        if (userId != ((User) HttpContext.Items["User"]).Id)
                            throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                        if (((User) HttpContext.Items["User"]).OrganizationId != null)
                            user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                        else
                            throw new Exception("Data integrity broken!\ncustomers must have an assigned organization");
                        break;
                    case 2:
                        user.PermissionLevel = 2;
                        if (userId != ((User) HttpContext.Items["User"]).Id)
                            throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                        if (((User) HttpContext.Items["User"]).OrganizationId != null)
                            user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                        else
                            throw new Exception("Data integrity broken!\nemployees must have an assigned organization");
                        break;
                    case 3:
                        user.PermissionLevel = 3;
                        if (current.OrganizationId != ((User) HttpContext.Items["User"]).OrganizationId || current.PermissionLevel >= ((User) HttpContext.Items["User"]).PermissionLevel)
                            throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                        if (((User) HttpContext.Items["User"]).OrganizationId != null)
                            user.OrganizationId = ((User) HttpContext.Items["User"]).OrganizationId;
                        else
                            throw new Exception("Data integrity broken!\nemployers must have an assigned organization");
                        break;
                    case 4:
                        user.PermissionLevel = 4;
                        if (current.PermissionLevel >= ((User) HttpContext.Items["User"]).PermissionLevel)
                            throw new ForbiddenException("You don't have high enough security clearance for this operation!");
                        break;
                    case 5:
                        if (user.PermissionLevel != null)
                        {
                            if (user.PermissionLevel > 5)
                                user.PermissionLevel = 5;
                        }
                        else
                        {
                            if (user.Permission != null)
                            {
                                int level = _persistence.GetPermissionLevel(user.Permission);
                                if (level >= 5)
                                    user.PermissionLevel = 5;
                                else
                                    user.PermissionLevel = level;
                            }
                        }
                        break;
                }
                if (user.PermissionLevel <= 3 && user.OrganizationId == null)
                    throw new ConflictException("customers, employees and employers must have an assigned organization");
                _persistence.UpdateUser(user);
                return StatusCode(200);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }

        private static void CheckPassword(string password)
        {
            //check password constraints here, throw conflict exception if needed
            if (password == null)
                throw new ConflictException("password can't be empty string");
            if (password.Length <= 8)
                throw new ConflictException("password must be longer than 8 characters");
            if (!Regex.Match(password, "[0-9]").Success)
                throw new ConflictException("password must contain at least one number");
            if (!Regex.Match(password, "[A-Z]").Success)
                throw new ConflictException("password must contain at least one uppercase letter");
            if (!Regex.Match(password, "[a-z]").Success)
                throw new ConflictException("password must contain at least one lowercase letter");
        }

        private static void CheckUsername(string username)
        {
            //check username constraints here, throw conflict exception if needed
            if (username == null)
                throw new ConflictException("username can't be empty string");
            if (Regex.Match(username, "[^0-9a-zA-Z_]").Success)
                throw new ConflictException("username can only contain letters, numbers and underscores");
        }

        [HttpGet]
        [Route("token")]
        public IActionResult GetToken()
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                return StatusCode(200, GenerateJwtToken((int) ((User)HttpContext.Items["User"]).Id));
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpGet]
        [Route("user/id/{id}")]
        public IActionResult GetUserById([FromRoute] int id)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                if (((User)HttpContext.Items["User"]).PermissionLevel < 4 && ((User)HttpContext.Items["User"]).OrganizationId != _persistence.GetUserById(id).OrganizationId)
                    throw new ForbiddenException("You don't have high enough clearance for this operation!");
                User temp = _persistence.GetUserById(id);
                return StatusCode(200, temp);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpGet]
        [Route("user/name/{user}")]
        public IActionResult GetUserByName([FromRoute] string user)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                if (((User)HttpContext.Items["User"]).PermissionLevel < 4 && ((User)HttpContext.Items["User"]).OrganizationId != _persistence.GetUserByName(user).OrganizationId)
                    throw new ForbiddenException("You don't have high enough clearance for this operation!");
                User temp = _persistence.GetUserByName(user);
                return StatusCode(200, temp);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (ForbiddenException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpGet]
        [Route("user/me")]
        public IActionResult GetAuthenticatedUser()
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User temp = (User) HttpContext.Items["User"];
                return StatusCode(200, temp);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(401, e.Message);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }

        private string GenerateJwtToken(int userId)
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var key = _config.JwtKey;
            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(new[] { new Claim("id", userId.ToString()) }),
                SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
            };
            if (_config.TokenExpire > 0)
                tokenDescriptor.Expires = DateTime.UtcNow.Add(new TimeSpan(0, _config.TokenExpire, 0));
            else
                tokenDescriptor.Expires = DateTime.Parse("01/01/2030");
            var token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }
        
        [HttpGet]
        [Route("health")]
        public IActionResult HealthCheck()
        {
            return StatusCode(200);
        }
    }
}