namespace EventManagerBackend.Model.Exception
{
    public class UnauthorizedException : System.Exception
    {
        public UnauthorizedException(string message) : base(message) {}
    }
}