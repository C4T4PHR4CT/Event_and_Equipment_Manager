using System;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.IdentityModel.Tokens;

namespace EventManagerBackend.Data
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly IPersistenceService _persistence;
        private readonly IConfigService _config;

        public JwtMiddleware(RequestDelegate next, IPersistenceService persistence, IConfigService config)
        {
            _next = next;
            _persistence = persistence;
            _config = config;
        }

        public async Task Invoke(HttpContext context)
        {
            var temp = context.Request.Headers["Authorization"];
            foreach (string token in temp)
                if (AttachBasic(context, token))
                    break;
            foreach (string token in temp)
                if (AttachBearer(context, token))
                    break;
            await _next(context);
        }

        private bool AttachBearer(HttpContext context, string token)
        {
            try {
                var temp = token.Split(" ");
                if (temp[0] != "Bearer" || temp.Length != 2)
                    throw new Exception();
                token = temp[1];
                var tokenHandler = new JwtSecurityTokenHandler();
                var key = Encoding.ASCII.GetBytes(_config.Secret);
                tokenHandler.ValidateToken(token, new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(key),
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    ClockSkew = TimeSpan.Zero
                }, out SecurityToken validatedToken);

                var jwtToken = (JwtSecurityToken) validatedToken;
                int userId = int.Parse(jwtToken.Claims.First(x => x.Type == "id").Value);

                context.Items["User"] = _persistence.GetUserById(userId);
                return true;
            } catch {
                return false;
            }
        }
        
        private bool AttachBasic(HttpContext context, string token)
        {
            try {
                var temp = token.Split(" ");
                if (temp[0] != "Basic" || temp.Length != 2)
                    throw new Exception();
                temp = Encoding.UTF8.GetString(Convert.FromBase64String(temp[1])).Split(":");
                if (temp.Length != 2)
                    throw new Exception();
                if (_persistence.CheckUserPassword(temp[0], temp[1]))
                    context.Items["User"] = _persistence.GetUser(temp[0]);
                else
                    throw new Exception();
                return true;
            } catch {
                return false;
            }
        }
    }
}