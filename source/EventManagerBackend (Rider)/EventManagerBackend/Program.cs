using EventManagerBackend.Data;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Hosting;

namespace EventManagerBackend
{
    public class Program
    {
        public static void Main(string[] args)
        {
            int port = new ConfigService().Port;
            string[] ports = {"http://*:" + port, "https://*:" + (port + 1)};
            IHostBuilder hostBuilder = Host.CreateDefaultBuilder(args).ConfigureWebHostDefaults(webBuilder => { webBuilder.UseStartup<Startup>()/*.UseUrls(ports)*/; });
            hostBuilder.Build().Run();
        }
    }
}