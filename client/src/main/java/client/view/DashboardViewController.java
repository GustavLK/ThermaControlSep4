package client.view;

import client.viewmodel.DashboardViewModel;
import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import shared.dto.SensorDataDTO;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardViewController implements BaseViewController, PropertyChangeListener {

    @FXML private ListView<String> pumpListView;
    @FXML private TextField searchField;
    @FXML private Label pumpNameLabel;
    @FXML private Label pumpIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label waterflowLabel;
    @FXML private Label energyLabel;
    @FXML private Label tempLabel;
    @FXML private Label copLabel;
    @FXML private LineChart<String, Number> waterflowChart;
    @FXML private Label alarmLabel;

    private ViewHandler viewHandler;
    private DashboardViewModel dashboardViewModel;

    // Track pumps
    private List<Integer> clientIds = new ArrayList<>();
    private Map<Integer, String> clientNames = new HashMap<>();
    private Map<Integer, List<Double>> waterFlowHistory = new HashMap<>();
    private Map<Integer, List<String>> timeHistory = new HashMap<>();
    private int selectedClientId = -1;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
        this.dashboardViewModel = viewModelHandler.getDashboardViewModel();
        dashboardViewModel.addListener(this);

        setupListView();
    }

    private void setupListView() {
        pumpListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < clientIds.size()) {
                selectedClientId = clientIds.get(index);
                showPump(selectedClientId);
            }
        });

        searchField.textProperty().addListener((obs, old, newVal) -> updateList(newVal));
    }

    private void updateList(String search) {
        List<String> items = new ArrayList<>();
        for (int id : clientIds) {
            String name = clientNames.getOrDefault(id, "Pump " + id);
            if (search == null || search.isEmpty()
                    || name.toLowerCase().contains(search.toLowerCase())) {
                items.add(name + "  |  HP-00" + id);
            }
        }
        pumpListView.setItems(FXCollections.observableArrayList(items));
    }

    private void showPump(int clientId) {
        String name = clientNames.getOrDefault(clientId, "Pump " + clientId);
        pumpNameLabel.setText(name);
        pumpIdLabel.setText("HP-00" + clientId);
        statusLabel.setText("Online");
        statusLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #0d2a1a; -fx-padding: 6 14; -fx-background-radius: 20;");

        SensorDataDTO latest = dashboardViewModel.getLatestForClient(clientId);
        if (latest != null) {
            waterflowLabel.setText(String.valueOf(latest.getWaterFlow()));
            tempLabel.setText(String.valueOf(latest.getTemperature()));
            energyLabel.setText(String.valueOf(latest.getEnergyConsumption()));
            copLabel.setText(String.valueOf(latest.getCOP()));
        }

        updateChart(clientId);
    }

    private void updateChart(int clientId) {
        waterflowChart.getData().clear();
        List<Double> history = waterFlowHistory.getOrDefault(clientId, new ArrayList<>());
        List<String> times = timeHistory.getOrDefault(clientId, new ArrayList<>());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Water Flow L/min");

        for (int i = 0; i < history.size(); i++) {
            series.getData().add(new XYChart.Data<>(times.get(i), history.get(i)));
        }

        waterflowChart.getData().add(series);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("sensorData")) {
            SensorDataDTO dto = (SensorDataDTO) evt.getNewValue();
            Platform.runLater(() -> {
                int id = dto.getClientId();

                // Register new pump if not seen before
                if (!clientIds.contains(id)) {
                    clientIds.add(id);
                    clientNames.put(id, "Pump " + id);
                    waterFlowHistory.put(id, new ArrayList<>());
                    timeHistory.put(id, new ArrayList<>());
                    updateList(searchField.getText());
                }

                // Update history (keep last 10 points)
                List<Double> wfHistory = waterFlowHistory.get(id);
                List<String> tHistory = timeHistory.get(id);
                wfHistory.add(dto.getWaterFlow());
                tHistory.add(LocalTime.now().format(TIME_FMT));
                if (wfHistory.size() > 10) {
                    wfHistory.remove(0);
                    tHistory.remove(0);
                }

                // Update displayed pump if selected
                if (id == selectedClientId) {
                    showPump(id);
                }
            });
        }
    }

    @FXML private void showDashboard() { viewHandler.openView(ViewScene.DASHBOARD); }
    @FXML private void showAlarmLog() { viewHandler.openView(ViewScene.ALARM_LOG); }
    @FXML private void showConfig() { viewHandler.openView(ViewScene.CONFIGURATION); }
    @FXML private void handleLogout() { viewHandler.openView(ViewScene.LOGIN); }

    @FXML
    private void handleAddPump() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Pump");
        dialog.setHeaderText("Enter pump name:");
        dialog.showAndWait().ifPresent(name -> {
            int newId = clientIds.size() + 1;
            clientIds.add(newId);
            clientNames.put(newId, name);
            waterFlowHistory.put(newId, new ArrayList<>());
            timeHistory.put(newId, new ArrayList<>());
            updateList(searchField.getText());
        });
    }

    @FXML
    private void handleRemovePump() {
        int index = pumpListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Pump");
        alert.setHeaderText("Are you sure?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int id = clientIds.remove(index);
                clientNames.remove(id);
                waterFlowHistory.remove(id);
                timeHistory.remove(id);
                updateList(searchField.getText());
                selectedClientId = -1;
                pumpNameLabel.setText("Select a pump");
                pumpIdLabel.setText("");
            }
        });
    }
}
