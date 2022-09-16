package diego.middle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class NodeOpRegisterController {
    @FXML
    private TextArea operationTxtArea;

    @FXML
    private TextArea nodeOnlineTxtArea;

    @FXML
    private Label nodePort;

    public void addOperation2Board(String operation) {
        if (operationTxtArea.getText() != null) {
            operationTxtArea.setText(operationTxtArea.getText() + operation + "\n");
        } else {
            operationTxtArea.setText(operation + "\n");
        }
    }

    public void addNodePort(int port) {
        nodePort.setText("Node port: " + port);
    }

    public void addNode2List(String node, boolean onlineOrNot) {
        //if true, online
        if (onlineOrNot) {
            if (nodeOnlineTxtArea.getText() != null) {
                nodeOnlineTxtArea.setText(nodeOnlineTxtArea.getText() + "Node online: " + node + "\n");
            } else {
                operationTxtArea.setText("Node online: " + node + "\n");
            }
        } else {
            if (nodeOnlineTxtArea.getText() != null) {
                nodeOnlineTxtArea.setText(nodeOnlineTxtArea.getText() + "Node offline: " + node + "\n");
            } else {
                operationTxtArea.setText("Node offline: " + node + "\n");
            }
        }
    }

    public void removeNode(ActionEvent event) {
        System.out.println("remove node");
    }
}