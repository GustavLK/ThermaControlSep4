package client.view;

import client.viewmodel.ConfigViewModel;
import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfigViewController implements BaseViewController {

    @FXML private ListView<String> clientList;
    @FXML private TextField waterFlowMinField;
    @FXML private TextField waterFlowMaxField;
    @FXML private TextField tempMinField;
    @FXML private TextField tempMaxField;
    @FXML private TextField copMinField;

    private ViewHandler viewHandler;
    private ConfigViewModel configViewModel;
    private ObservableList<String> clients = FXCollections.observableArrayList();

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
        this.configViewModel = viewModelHandler.getConfigViewModel();

        clientList.setItems(clients);
        loadDefaults();
    }

    private void loadDefaults() {
        waterFlowMinField.setText("3.0");
        waterFlowMaxField.setText("28.0");
        tempMinField.setText("-3.0");
        tempMaxField.setText("2.0");
        copMinField.setText("2.5");
    }

    @FXML
    public void addClientButtonPressed() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Client");
        dialog.setHeaderText("Enter client name:");
        dialog.showAndWait().ifPresent(name -> {
            clients.add(name);
            configViewModel.addClient(name);
        });
    }

    @FXML
    public void removeClientButtonPressed() {
        int index = clientList.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            clients.remove(index);
            configViewModel.removeClient(index);
        }
    }
    @FXML private void showPerformance() {
        viewHandler.openView(ViewScene.CONFIGURATION);
    }

    @FXML
    private void handleSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saved");
        alert.setHeaderText("Configuration saved!");
        alert.showAndWait();
    }

    @FXML private void showDashboard() { viewHandler.openView(ViewScene.DASHBOARD); }
    @FXML private void showAlarmLog() { viewHandler.openView(ViewScene.ALARM_LOG); }
    @FXML private void showConfig() { viewHandler.openView(ViewScene.CONFIGURATION); }
    @FXML private void handleLogout() { viewHandler.openView(ViewScene.LOGIN); }
}
