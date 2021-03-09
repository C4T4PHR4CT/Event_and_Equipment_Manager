using System.IO;
using System.Text.Json;

namespace EventManagerBackend.Data
{
    public class ConfigService : IConfigService
    {
        public int Port
        {
            get { 
                if (!_initialized)
                   Initialize();
                return _port ?? 42069;
            }
            set {
                if (!_initialized)
                    Initialize();
                _port = value;
                SaveConfig();
            }
        }
        private static int? _port;
        public string DbHost
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbHost ?? "localhost";
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbHost = value;
                SaveConfig();
            }
        }
        private static string _dbHost;
        public int DbPort
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbPort ?? 5432;
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbPort = value;
                SaveConfig();
            }
        }
        private static int? _dbPort;
        public string DbName
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbName ?? "postgres";
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbName = value;
                SaveConfig();
            }
        }
        private static string _dbName;
        public string DbUser
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbUser ?? "postgres";
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbUser = value;
                SaveConfig();
            }
        }
        private static string _dbUser;
        public string DbPassword
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbPassword ?? "postgres";
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbPassword = value;
                SaveConfig();
            }
        }
        private static string _dbPassword;
        public string DbSchema
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _dbSchema;
            }
            set {
                if (!_initialized)
                    Initialize();
                _dbSchema = value;
                SaveConfig();
            }
        }
        private static string _dbSchema;
        public string Secret
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _secret;
            }
            set {
                if (!_initialized)
                    Initialize();
                _secret = value;
                SaveConfig();
            }
        }
        private static string _secret;
        public bool CheckIntegrityOnStartup
        {
            get {
                if (!_initialized)
                    Initialize();
                return _checkIntegrityOnStartup ?? false;
            }
            set {
                if (!_initialized)
                    Initialize();
                _checkIntegrityOnStartup = value;
                SaveConfig();
            }
        }
        private static bool? _checkIntegrityOnStartup;
        public int TokenExpire
        {
            get {
                if (!_initialized)
                    Initialize();
                return _tokenExpire;
            }
            set {
                if (!_initialized)
                    Initialize();
                _tokenExpire = value;
                SaveConfig();
            }
        }
        private static int _tokenExpire;

        private static bool _initialized = false;

        public ConfigService()
        {
            Initialize();
        }

        private void SaveConfig()
        {
            try {
                string json = JsonSerializer.Serialize(this, new JsonSerializerOptions {WriteIndented = true});
                using StreamWriter file = new StreamWriter("config.json");
                file.WriteLine(json);
            } catch (IOException) {/*ignored*/}
        }

        private void Initialize()
        {
            if (_initialized) return;
            _initialized = true;
            using StreamReader file = new StreamReader("config.json");
            var json = file.ReadToEnd();
            JsonSerializer.Deserialize(json, typeof(ConfigService));
            file.Close();
        }
    }
}