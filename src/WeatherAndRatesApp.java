import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONObject;

public class WeatherAndRatesApp extends Application {
    private Service service;

    private Browser browser;

    @Override
    public void start(Stage primaryStage) {
        service = new Service("Poland");
        browser = new Browser();

        BorderPane borderPane = new BorderPane();

        // Location Box
        HBox locationBox = new HBox();

        Label countryLabel = new Label("Country: ");
        TextField countryTextField = new TextField("Poland");
        Label locationLabel = new Label("Location: ");
        TextField locationTextField = new TextField("Warsaw");

        Label currencyLabel = new Label("Currency: ");
        TextField currencyTextField = new TextField("USD");

        Button updateButton = new Button("Update");

        locationBox.getChildren().addAll(countryLabel, countryTextField, locationLabel,
                locationTextField, currencyLabel, currencyTextField, updateButton);

        // Weather Box
        HBox weatherBox = new HBox();

        Label weatherLabel = new Label();
        weatherBox.getChildren().add(weatherLabel);


        // Currency Box
        HBox currencyBox = new HBox();

        Label selectedCurrencyTextLabel = new Label("Selected rate: ");
        Label selectedCurrencyLabel = new Label();

        Label NBPtextLabel = new Label("Rate of PLN: ");
        Label NBPlabel = new Label();
        currencyBox.getChildren().addAll(selectedCurrencyTextLabel,selectedCurrencyLabel,
                NBPtextLabel,NBPlabel);

        HBox infoBar = new HBox();

        infoBar.getChildren().addAll(locationBox, weatherBox, currencyBox);
        HBox.setHgrow(locationBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(weatherBox, javafx.scene.layout.Priority.ALWAYS);

        borderPane.setTop(infoBar);
        borderPane.setCenter(browser);

        loadDefaultData(weatherLabel,selectedCurrencyLabel , NBPlabel);

        updateButton.setOnAction(e -> {
            String location = locationTextField.getText();
            String currency = currencyTextField.getText();
            String country = countryTextField.getText();
            loadData(country, location, currency, weatherLabel, selectedCurrencyLabel, NBPlabel);
        });

        Scene scene = new Scene(borderPane, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Money Rain");
        Image appIcon = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/assets/moneyrain.jpg")));
        primaryStage.getIcons().add(appIcon);
        primaryStage.show();
    }

    private void loadData(String country, String location, String currency, Label weather, Label rate, Label rateNBP) {
        new Thread(() -> {
            service.setCountry(country);

            String weatherJson = service.getWeather(location);
            Double rateValue = service.getRateFor(currency);
            Double rateNBPValue = service.getNBPRate();

            JSONObject obj = new JSONObject(weatherJson);
            double temperatureInKelvin = obj.getJSONObject("main").getDouble("temp");
            String weatherDescription = obj.getJSONArray("weather").getJSONObject(0).getString("description");

            // Przeliczanie temperatury na stopnie Celsjusza lub inne przetwarzanie
            int temperatureInCelsius = (int) (temperatureInKelvin - 273.15);

            // Przygotowanie tekstu do wyświetlenia
            String finalWeatherText = temperatureInCelsius + "°C, " + weatherDescription;

            Platform.runLater(() -> {
                weather.setText(finalWeatherText);
                rate.setText(String.format("%.2f", rateValue));
                rateNBP.setText(String.format("%.2f", rateNBPValue));

                browser.loadPage(location);
            });
        }).start();
    }

    private void loadDefaultData(Label weather, Label rate, Label rateNBP) {
        loadData("Poland","Warsaw", "USD", weather, rate, rateNBP);

        browser.loadPage("Warsaw");
    }
}
