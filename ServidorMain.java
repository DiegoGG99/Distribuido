package diego.servidor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServidorMain extends Application {
    private ServidorCliente server;
    private int serverPort = 10001;

    @Override
    public void start(Stage stage) throws IOException {
        this.server = new ServidorCliente("localhost", serverPort);
        server.connect();

        if (server.login("server", "server")) {
            FXMLLoader fxmlLoader = new FXMLLoader(ServidorMain.class.getResource("operationList.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Servidor " + serverPort);
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}