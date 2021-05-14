using System.Text.Json.Serialization;

namespace EventManagerBackend.Model
{
    public class User
    {
        [JsonPropertyName("id")]
        public int? Id { get; set; }
        [JsonPropertyName("name")]
        public string Name { get; set; }
        [JsonPropertyName("password")]
        public string Password { get; set; }
        [JsonIgnore]
        public int? PermissionLevel { get; set; }
        [JsonPropertyName("permission")]
        public string Permission { get; set; }
        [JsonIgnore]
        public int? OrganizationId { get; set; }
        [JsonPropertyName("organization")]
        public string Organization { get; set; }
    }
}