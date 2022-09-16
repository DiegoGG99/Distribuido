package diego.servidor;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServidorApp extends Application {
    private ServidorCliente server;
    private int serverPort = 8801;

    @Override
    public void start(Stage stage) throws IOException {
        this.server = new ServidorCliente("localhost", serverPort);
        server.connect();

        if (server.login("server", "server")) {
            FXMLLoader fxmlLoader = new FXMLLoader(ServidorApp.class.getResource("operationList.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Server " + serverPort);
            stage.setScene(scene);
            stage.show();

            ControllerOps controller = fxmlLoader.getController();
            server.setController(controller);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}