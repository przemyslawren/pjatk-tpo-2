
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Currency;
import java.util.Locale;
import org.json.JSONObject;

public class Service {
    private volatile Locale locale;

    Service(String country) {
        this.locale = getLocaleFromName(country);
        System.out.println(locale);
    }

    private Locale getLocaleFromName(String country) {
        country = country.replaceAll("\\s", "").toLowerCase();

        for (Locale locale : Locale.getAvailableLocales())
            if (country.equals(locale.getDisplayCountry(Locale.ENGLISH).toLowerCase()))
                return locale;
        return null;
    }

    public void setCountry(String country) {
        this.locale = getLocaleFromName(country);
        System.out.println("Updated locale: " + this.locale);
    }

    String getWeather(String city) {
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=";
        String apiKey = "3bf47a4cc9cb78dce3f01b190307fb6b";
        String fullUrl = weatherUrl + city + "," + locale.getCountry() + "&appid=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return "City not found. Please check the city name and country";
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Data not found";
        }
    }

    // zwraca kurs waluty danego kraju wobec waluty podanej jako argument
    Double getRateFor(String currency) {
        String fullUrl = "https://open.er-api.com/v6/latest/" + currency;
        Double rate = null;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject obj = new JSONObject(response.body());

            Double selectedCurrency = obj.getJSONObject("rates")
                    .getDouble(currency);

            Double countryCurrency = obj.getJSONObject("rates")
                    .getDouble(String.valueOf(Currency.getInstance(locale)));

            rate = selectedCurrency/countryCurrency;

            return rate;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return rate;
        }
    }

    // zwraca kurs złotego wobec waluty danego kraju
    Double getNBPRate() {

        String countryCode = locale.getCountry();
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        String fullUrl = "https://open.er-api.com/v6/latest/PLN";
        Double rate = null;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject obj = new JSONObject(response.body());

            Double PLN = obj.getJSONObject("rates")
                    .getDouble("PLN");

            // Sprawdzenie, czy odpowiedź zawiera walutę kraju
            if(obj.getJSONObject("rates").has(currencyCode)) {
                Double countryCurrencyRate = obj.getJSONObject("rates").getDouble(currencyCode);
                rate = 1 / countryCurrencyRate; // Obliczenie kursu waluty kraju względem PLN
            } else {
                System.out.println("Currency not found in response.");
                return null;
            }
            return rate;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return rate;
        }
    }
}
