using System.Text.Json.Serialization;

namespace EventManagerBackend.Model
{
    public class Event
    {
        [JsonPropertyName("id")]
        public int? Id { get; set; }
        [JsonPropertyName("name")]
        public string Name { get; set; }
        [JsonPropertyName("hidden")]
        public bool Hidden { get; set; }
        [JsonPropertyName("start")]
        public long Start { get; set; }
        [JsonPropertyName("end")]
        public long End { get; set; }
        [JsonPropertyName("equipments")]
        public Equipment[] Equipments { get; set; }
    }
}