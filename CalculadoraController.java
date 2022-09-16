package diego.calculadora;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CalculadoraController implements Initializable {
    private CalculadoraCliente client;

    public void setClient(CalculadoraCliente client) {
        this.client = client;
    }

    @FXML
    private Label expressionLabel;
    @FXML
    private Label resultLabel;

    public void insertNumber(String number) {
        expressionLabel.setText(expressionLabel.getText() + number);
    }

    public void insertOperator(String operator) {
        expressionLabel.setText(expressionLabel.getText() + " " + operator + " ");
    }

    public void onMouseClick(MouseEvent mouseEvent) throws IOException {
        // Object cast as Button
        Button button = (Button) mouseEvent.getSource();
        String btnText = button.getText();

        switch (btnText) {
            case "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" -> insertNumber(btnText);
            case "+", "-", "*", "/" -> insertOperator(btnText);
            case "CLEAR" -> clearExpression();
            case "ANSWER" -> setResult();
            //handled by the server
            case "=" -> {
                System.out.println("operation sent to server");
                handleOperation(client, expressionLabel.getText());
            }
            case "RESULT" -> handleResult();
        }
    }

    private void handleResult() {
        String result = client.getResult();
        System.out.println("sandia " + result);
        if (!(result == null || result.equals(""))) {
            resultLabel.setText(result);
        }
    }

    private void setResult() {
        //get the result from client, then display it in the label
        String result = resultLabel.getText();
        if (!(result == null || Objects.equals(result, "= "))) {
            expressionLabel.setText(result);
        }
    }

    private void handleOperation(CalculadoraCliente client, String operation) throws IOException {
        System.out.println(operation + " to server..");
        client.op(operation);
    }

    public void setAnswer(String result) {
        resultLabel.setText(result);
    }

    private void clearExpression() {
        expressionLabel.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultLabel.setText("= ");
    }
}