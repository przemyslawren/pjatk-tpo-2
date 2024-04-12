import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherRatesApp extends Application {
    private Service service;
    private WebBrowser webBrowser;

    @Override
    public void start(Stage primaryStage) {
        service = new Service("Poland");
        webBrowser = new WebBrowser();

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5));

        // Text Fields Box
        HBox textFieldsBox = new HBox();
        textFieldsBox.setSpacing(5);

        Label countryLabel = new Label("Country: ");
        countryLabel.setStyle("-fx-font-size: 16px");
        TextField countryTextField = new TextField("Poland");

        countryTextField.setStyle("-fx-font-size: 14px");
        Label locationLabel = new Label("Location: ");
        TextField locationTextField = new TextField("Warsaw");

        Label currencyLabel = new Label("Currency rate: ");
        TextField currencyTextField = new TextField("USD");

        Button updateButton = new Button("Update");

        textFieldsBox.getChildren().addAll(countryLabel, countryTextField, locationLabel,
                locationTextField, currencyLabel, currencyTextField, updateButton);

        // Label Box
        HBox labelBox = new HBox();
        labelBox.setSpacing(5);
        Label weatherTextLabel = new Label("Weather: ");
        Label weatherLabel = new Label();
        Label selectedCurrencyTextLabel = new Label("Rate comparison: ");
        Label selectedCurrencyLabel = new Label();

        Label NBPtextLabel = new Label("Rate for PLN: ");
        Label NBPlabel = new Label();
        labelBox.getChildren().addAll(weatherTextLabel, weatherLabel, selectedCurrencyTextLabel,
                selectedCurrencyLabel,
                NBPtextLabel,NBPlabel);

        HBox infoBar = new HBox();
        infoBar.getStyleClass().add("infobar-style");
        infoBar.setPadding(new Insets(10));
        infoBar.getChildren().addAll(textFieldsBox, labelBox);
        HBox.setHgrow(textFieldsBox, javafx.scene.layout.Priority.ALWAYS);

        borderPane.setTop(infoBar);
        borderPane.setCenter(webBrowser);

        loadDefaultData(weatherLabel,selectedCurrencyLabel , NBPlabel);

        updateButton.setOnAction(e -> {
            String location = locationTextField.getText();
            String currency = currencyTextField.getText();
            String country = countryTextField.getText();
            loadData(country, location, currency, weatherLabel, selectedCurrencyLabel, NBPlabel);
        });

        Scene scene = new Scene(borderPane, 1920, 1080);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("assets/style.css")).toExternalForm());
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

            try {
                JSONObject obj = new JSONObject(weatherJson);

                double temperatureInKelvin = obj.getJSONObject("main").getDouble("temp");
                String weatherDescription = obj.getJSONArray("weather").getJSONObject(0).getString("description");
                int temperatureInCelsius = (int) (temperatureInKelvin - 273.15);

                weatherJson = temperatureInCelsius + "°C, " + weatherDescription;

            } catch (JSONException e) {
                weatherJson = "City not found";
            }
            String finalWeatherText = weatherJson;

            Platform.runLater(() -> {
                weather.setText(finalWeatherText);
                rate.setText(String.format("%.2f", rateValue));
                rateNBP.setText(String.format("%.2f", rateNBPValue));

                webBrowser.loadPage(location);
            });
        }).start();
    }

    private void loadDefaultData(Label weather, Label rate, Label rateNBP) {
        loadData("Poland","Warsaw", "USD", weather, rate, rateNBP);

        webBrowser.loadPage("Warsaw");
    }
}
