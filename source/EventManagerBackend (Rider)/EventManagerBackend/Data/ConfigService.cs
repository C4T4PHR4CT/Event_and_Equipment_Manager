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
        public bool Https
        {
            get {
                if (!_initialized)
                    Initialize();
                return _https ?? true;
            }
            set {
                if (!_initialized)
                    Initialize();
                _https = value;
                SaveConfig();
            }
        }
        private static bool? _https;
        public bool Swagger
        {
            get {
                if (!_initialized)
                    Initialize();
                return _swagger ?? false;
            }
            set {
                if (!_initialized)
                    Initialize();
                _swagger = value;
                SaveConfig();
            }
        }
        private static bool? _swagger;
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
        public byte[] JwtKey
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _jwtKey;
            }
            set {
                if (!_initialized)
                    Initialize();
                _jwtKey = value;
                SaveConfig();
            }
        }
        private static byte[] _jwtKey;
        public byte[] Salt
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _salt;
            }
            set {
                if (!_initialized)
                    Initialize();
                _salt = value;
                SaveConfig();
            }
        }
        private static byte[] _salt;
        public bool ReInitializeDb
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _reInitializeDb ?? false;
            }
            set {
                if (!_initialized)
                    Initialize();
                _reInitializeDb = value;
                SaveConfig();
            }
        }
        private static bool? _reInitializeDb;
        public int TokenExpire
        {
            get {
                if (!_initialized)
                    Initialize();
                return _tokenExpire ?? 0;
            }
            set {
                if (!_initialized)
                    Initialize();
                _tokenExpire = value;
                SaveConfig();
            }
        }
        private static int? _tokenExpire;
        public int UserPostPermissionLevel
        {
            get { 
                if (!_initialized)
                    Initialize();
                return _userPostPermissionLevel ?? 4;
            }
            set {
                if (!_initialized)
                    Initialize();
                _userPostPermissionLevel = value;
                SaveConfig();
            }
        }
        private static int? _userPostPermissionLevel;

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