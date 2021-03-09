namespace EventManagerBackend.Data
{
    public interface IConfigService
    {
        public int Port { get; set; }
        public string DbHost { get; set; }
        public int DbPort { get; set; }
        public string DbName { get; set; }
        public string DbUser { get; set; }
        public string DbPassword { get; set; }
        public string DbSchema { get; set; }
        public string Secret { get; set; }
        public bool CheckIntegrityOnStartup { get; set; }
        public int TokenExpire { get; set; }
    }
}