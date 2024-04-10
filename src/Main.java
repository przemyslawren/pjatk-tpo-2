import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        System.out.println(weatherJson);
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        System.out.println(rate1);
        System.out.println(rate2);
        // ...
        // część uruchamiająca GUI
        Application.launch(View.class);
    }
}