using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace EventManagerBackend.Model
{
    public class Author
    {
        [JsonPropertyName("id")] public int? Id { set; get; }
        [JsonPropertyName("first_name")] public string FirstName { set; get; }
        [JsonPropertyName("last_name")] public string LastName { set; get; }
        [JsonPropertyName("books")] public List<Book> Books { set; get; }
    }
}