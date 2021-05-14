using System.Security.Cryptography;
using EventManagerBackend.Data;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.OpenApi.Models;

namespace EventManagerBackend
{
    public class Startup
    {
        private IConfigService _config;
        
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }
        
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddControllers();
            _config = new ConfigService();
            PersistenceServiceMs persistenceService = new PersistenceServiceMs(_config);
            if (_config.ReInitializeDb)
            {
                var bytes1 = new byte[128 / 8];
                var bytes2 = new byte[128 / 8];
                using var generator = RandomNumberGenerator.Create();
                generator.GetBytes(bytes1);
                generator.GetBytes(bytes2);
                _config.Salt = bytes1;
                _config.JwtKey = bytes2;
                persistenceService.DropSchema();
                persistenceService.InitSchema();
                persistenceService.CheckIntegrity();
            }
            services.AddSingleton<IConfigService, IConfigService>(init => _config);
            services.AddSingleton<IPersistenceService, PersistenceServiceMs>(init => persistenceService);
            services.AddSingleton<ILogService, LogService>();
            services.AddSwaggerGen(c => {
                c.SwaggerDoc("v1", new OpenApiInfo {Title = "Event and Equipment Manager API", Version = "v1"});
                // add JWT Authentication
                var securityScheme = new OpenApiSecurityScheme
                {
                    Name = "Bearer Authentication",
                    In = ParameterLocation.Header,
                    Type = SecuritySchemeType.Http,
                    Scheme = "bearer",
                    BearerFormat = "JWT",
                    Reference = new OpenApiReference {
                        Id = JwtBearerDefaults.AuthenticationScheme,
                        Type = ReferenceType.SecurityScheme
                    }
                };
                c.AddSecurityDefinition(securityScheme.Reference.Id, securityScheme);
                c.AddSecurityRequirement(new OpenApiSecurityRequirement {
                    {securityScheme, new string[] { }}
                });
                // add Basic Authentication
                var basicSecurityScheme = new OpenApiSecurityScheme {
                    Type = SecuritySchemeType.Http,
                    Scheme = "basic",
                    Reference = new OpenApiReference {
                        Id = "BasicAuth",
                        Type = ReferenceType.SecurityScheme
                    }
                };
                c.AddSecurityDefinition(basicSecurityScheme.Reference.Id, basicSecurityScheme);
                c.AddSecurityRequirement(new OpenApiSecurityRequirement
                {
                    {basicSecurityScheme, new string[] { }}
                });
            });
        }
        
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment()) 
            {
                app.UseDeveloperExceptionPage();
            }
            if (env.IsDevelopment() || _config.Swagger)
            {
                app.UseSwagger();
                app.UseSwaggerUI(c => c.SwaggerEndpoint("/swagger/v1/swagger.json", "AuthorAPI v1"));
            }

            app.UseRouting();

            app.UseMiddleware<JwtMiddleware>();

            app.UseEndpoints(endpoints => { endpoints.MapControllers(); });
        }
    }
}