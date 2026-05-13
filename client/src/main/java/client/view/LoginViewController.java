package client.view;

import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginViewController implements BaseViewController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ViewHandler viewHandler;

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("Gustav") && password.equals("123")) {
            viewHandler.openView(ViewScene.DASHBOARD);
        } else {
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
}
