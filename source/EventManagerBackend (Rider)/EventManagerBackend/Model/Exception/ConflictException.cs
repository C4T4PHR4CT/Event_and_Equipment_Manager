namespace EventManagerBackend.Model.Exception
{
    public class ConflictException : System.Exception
    {
        public ConflictException(string message) : base(message) {}
    }
}