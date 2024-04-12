
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Service {
    private Locale locale;

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
        String apiKey = "YOUR_API_KEY";
        String fullUrl = weatherUrl + city + "," + locale.getCountry() + "&appid=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();
        HttpResponse<String> response;

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

    Double getRateFor(String currency) {
        String fullUrl = "https://open.er-api.com/v6/latest/" + currency;
        double rate;

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

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Double getNBPRate() {
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        String[] urls = {
                "https://static.nbp.pl/dane/kursy/xml/a073z240412.xml",
                "https://static.nbp.pl/dane/kursy/xml/b015z240410.xml",
                "https://static.nbp.pl/dane/kursy/xml/c073z240412.xml"
        };

        HttpClient client = HttpClient.newHttpClient();
        Map<String, Double> currencyMap = new HashMap<>();

        for (String url : urls) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                currencyMap.putAll(parseNBPResponse(response.body()));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (currencyCode.equals("PLN")) {
            return 1.0;
        }

        return currencyMap.get(currencyCode);
    }

    static Map<String, Double> parseNBPResponse(String xmlData) {
        Document doc = Jsoup.parse(xmlData, "", org.jsoup.parser.Parser.xmlParser());
        Elements positions = doc.select("pozycja");
        Map<String, Double> currencyRates = new HashMap<>();

        for (Element position : positions) {
            String currencyCode = position.select("kod_waluty").text();
            String rateString = position.select("kurs_sredni").text()
                    .replace(',', '.');

            if (rateString.contains(".")) {
                Double exchangeRate = Double.parseDouble(rateString);

                currencyRates.put(currencyCode, exchangeRate);
            }
        }
        return currencyRates;
    }
}
