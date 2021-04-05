using EventManagerBackend.Data;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Hosting;

namespace EventManagerBackend
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var config = new ConfigService();
            int port = config.Port;
            string[] ports;
            if (config.Https)
                ports = new []{"http://*:" + port, "https://*:" + (port + 1)};
            else
                ports = new []{"http://*:" + port};
            IHostBuilder hostBuilder = Host.CreateDefaultBuilder(args).ConfigureWebHostDefaults(webBuilder => { webBuilder.UseStartup<Startup>().UseUrls(ports); });
            hostBuilder.Build().Run();
        }
    }
}