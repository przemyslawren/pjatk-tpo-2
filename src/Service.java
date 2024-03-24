public class Service {
    Service(String country) {
        System.out.println("Service created" + " for " + country);
    }

    // zwraca informację o pogodzie w podanym mieście danego kraju w formacie JSON (to ma być pełna informacja
    // uzyskana z serwisu openweather - po prostu tekst w formacie JSON)s
    private String getWeather(String miasto) {
        return "Pogoda w " + miasto + " jest ładna";
    }
    // zwraca kurs waluty danego kraju wobec waluty podanej jako argument
    private Double getRateFor(String kod_waluty) {
        return 4.25;
    }
    // zwraca kurs złotego wobec waluty danego kraju
    private Double getNBPRate() {
        return 4.25;
    }

}
