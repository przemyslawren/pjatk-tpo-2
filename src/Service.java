import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Service {
    private final String apiKey = "3bf47a4cc9cb78dce3f01b190307fb6b";

    Service(String country) {

    }

    // zwraca informację o pogodzie w podanym mieście danego kraju w formacie JSON (to ma być pełna informacja
    // uzyskana z serwisu openweather - po prostu tekst w formacie JSON)s
    String getWeather(String city) {
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=";
        String apiUrl = "&appid=" + this.apiKey;
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String fullUrl = weatherUrl + encodedCity + apiUrl;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException(
                        "Failed to fetch weather data: HTTP Response code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching weather data");
        }
    }
        // zwraca kurs waluty danego kraju wobec waluty podanej jako argument
        private Double getRateFor (String kod_waluty){
            return 4.25;
        }
        // zwraca kurs złotego wobec waluty danego kraju
        private Double getNBPRate () {
            return 4.25;
        }
    }
