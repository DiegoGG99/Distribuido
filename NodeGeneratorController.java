package diego.middle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NodeGeneratorController {
    int nodePort = 10000;

    @FXML
    Button newNodeBtn;

    public void newNode(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MiddleApp.class.getResource("node-operation-register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Generator");
        stage.setScene(scene);
        stage.show();

        //TO-DO: generate new node
        NodeOpRegisterController controller = fxmlLoader.getController();
        controller.addNodePort(nodePort);
        NodeMOM node = new NodeMOM(nodePort);
        node.setController(controller);
        node.start();

        nodePort++;
    }
}