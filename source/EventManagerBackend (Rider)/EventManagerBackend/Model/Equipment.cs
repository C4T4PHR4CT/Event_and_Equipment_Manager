using System.Text.Json.Serialization;

namespace EventManagerBackend.Model
{
    public class Equipment
    {
        [JsonPropertyName("id")]
        public int? Id { get; set; }
        [JsonPropertyName("name")]
        public string Name { get; set; }
        [JsonPropertyName("category")]
        public string Category { get; set; }
        [JsonPropertyName("events")]
        public Event[] Events { get; set; }
    }
}