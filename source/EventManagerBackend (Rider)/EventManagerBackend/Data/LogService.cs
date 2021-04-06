using System;
using System.IO;

namespace EventManagerBackend.Data
{
    public class LogService : ILogService
    {
        public LogService()
        {
            
        }
        
        public void Log(string value)
        {
            try
            {
                string temp = "[LOG " + DateTime.Now.ToString("dd/MM/yyyy HH.mm.ss") + "]\n" + value + "\n\n";
                using StreamWriter file = new StreamWriter("log.txt", true);
                file.WriteLine(temp);
                Console.WriteLine(temp);
            } catch (IOException) {/*ignored*/}
        }
    }
}