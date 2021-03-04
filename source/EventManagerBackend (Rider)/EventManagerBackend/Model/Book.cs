using System.Text.Json.Serialization;

namespace EventManagerBackend.Model
{
    public class Book
    {
        [JsonPropertyName("isbn")] public int ISBN { set; get; }
        [JsonPropertyName("title")] public string Title { set; get; }
        [JsonPropertyName("publication_year")] public int PublicationYear { set; get; }
        [JsonPropertyName("number_of_pages")] public int NumOfPages { set; get; }
        [JsonPropertyName("genre")] public string Genre { set; get; }
    }
}