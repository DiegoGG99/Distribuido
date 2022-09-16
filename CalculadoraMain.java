package diego.calculadora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CalculadoraMain extends Application {
    private CalculadoraCliente client;
    private int serverPort = 10002;

    @Override
    public void start(Stage stage) throws IOException {
        this.client = new CalculadoraCliente("localhost", serverPort);
        client.connect();

        if (client.login("guest", "guest")) {
            FXMLLoader fxmlLoader = new FXMLLoader(CalculadoraMain.class.getResource("calculadora.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Calculator " + serverPort);
            stage.setScene(scene);
            stage.show();

            CalculadoraController controller = fxmlLoader.getController();
            controller.setClient(client);

        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(CalculadoraMain.class.getResource("errorPanel.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Server down");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}