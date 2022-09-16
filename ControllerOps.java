package diego.servidor;

import javafx.fxml.FXML;

import javafx.scene.control.TextArea;

public class ControllerOps {
    @FXML
    private TextArea serverTxtArea;

    public void addOperation2List(String operation) {
        if (serverTxtArea.getText() != null)
            serverTxtArea.setText(serverTxtArea.getText() + operation + "\n");
        else
            serverTxtArea.setText(operation + "\n");
    }
}
