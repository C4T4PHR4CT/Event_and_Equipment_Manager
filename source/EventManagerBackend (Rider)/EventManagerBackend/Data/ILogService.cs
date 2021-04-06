using System.Runtime.CompilerServices;

namespace EventManagerBackend.Data
{
    public interface ILogService
    {
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Log(string value);
    }
}