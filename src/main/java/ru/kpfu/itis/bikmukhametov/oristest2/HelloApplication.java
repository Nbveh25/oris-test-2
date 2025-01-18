package ru.kpfu.itis.bikmukhametov.oristest2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloApplication extends Application {
    private TextArea chatArea;
    private TextField inputField;
    private boolean waitingForCity = false;
    private boolean waitingForCurrency = false;
    private String currencyToCheck = "";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        chatArea = new TextArea();
        inputField = new TextField();

        inputField.setOnAction(event -> processCommand(inputField.getText()));

        VBox vbox = new VBox(chatArea, inputField);
        scene.setRoot(vbox);

        stage.setTitle("LOL-KEK-CHEBUREK!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void processCommand(String command) {
        if (waitingForCity) {
            getWeather(command);
            waitingForCity = false;
        } else if (waitingForCurrency) {
            currencyToCheck = command.toUpperCase();
            getCurrencyRates(currencyToCheck);
            waitingForCurrency = false;
        }
        switch (command.toLowerCase()) {
            case "list":
                chatArea.appendText("Доступные команды: list, weather, exchange, quit\n");
                break;
            case "weather":
                chatArea.appendText("Введите город для получения погоды.\n");
                waitingForCity = true;
                break;
            case "exchange":
                chatArea.appendText("Введите валюту для получения курса (например, 'RUB').\n");
                waitingForCurrency = true;
                break;
            case "quit":
                chatArea.appendText("Выход на главную страницу.\n");
                System.exit(0);
                break;
            default:
                chatArea.appendText("Неизвестная команда. Введите 'list' для получения списка команд.\n");
        }
        inputField.clear();
    }

    private void getWeather(String city) {
        String apiKey = "ec6ae61f58c44f36d3bd7d4f99c9993a";
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            parseWeatherData(response.toString());
        } catch (Exception e) {
            chatArea.setText("Ошибка получения данных: " + e.getMessage());
        }
    }

    private void parseWeatherData(String jsonData) {
        JSONObject obj = new JSONObject(jsonData);
        String cityName = obj.getString("name");
        double temperature = obj.getJSONObject("main").getDouble("temp");
        String weatherDescription = obj.getJSONArray("weather").getJSONObject(0).getString("description");

        chatArea.setText("Город: " + cityName + "\n" +
                "Температура: " + temperature + "°C\n" +
                "Описание: " + weatherDescription);
    }

    private void getCurrencyRates(String currency) {
        String apiKey = "bb40537453064deba3d64eaf3f7a4d9a";
        String urlString = String.format("https://openexchangerates.org/api/latest.json?app_id=%s&symbols=%s,USD,EUR", apiKey, currency);

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            parseRateData(response.toString(), currency);
        } catch (Exception e) {
            chatArea.setText("Ошибка получения данных: " + e.getMessage());
        }
    }

    private void parseRateData(String jsonData, String currency) {
        JSONObject obj = new JSONObject(jsonData);
        JSONObject rates = obj.getJSONObject("rates");

        double currencyRate = rates.getDouble(currency);
        double usdRate = rates.getDouble("USD");
        double eurRate = rates.getDouble("EUR");

        chatArea.setText(String.format("Курс %s:\n1 %s = %.2f RUB\n1 USD = %.2f %s\n1 EUR = %.2f %s",
                currency, currency, currencyRate, usdRate, currency, eurRate, currency));
    }
}