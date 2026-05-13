package client.view;

import client.viewmodel.AlarmViewModel;
import client.viewmodel.DashboardViewModel;
import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
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
    private AlarmViewModel alarmViewModel;
    private int selectedClientId = -1;
    private int lastChartClientId = -1;

    private ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
        this.dashboardViewModel = viewModelHandler.getDashboardViewModel();
        this.alarmViewModel = viewModelHandler.getAlarmViewModel();

        dashboardViewModel.addListener(this);
        alarmViewModel.addListener(this);

        setupChart();
        setupListView();
        updateList(null);

        if (!dashboardViewModel.getClientIds().isEmpty()) {
            selectedClientId = dashboardViewModel.getClientIds().get(0);
            pumpListView.getSelectionModel().select(0);
            rebuildChart(selectedClientId);
            updateLabels(selectedClientId);
        }

        // Vis seneste alarm hvis der er nogen
        List<String> existingAlarms = alarmViewModel.getAlarmLog();
        if (!existingAlarms.isEmpty()) {
            alarmLabel.setText(existingAlarms.get(existingAlarms.size() - 1));
        }
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Water Flow L/min");
        series.setData(chartData);
        waterflowChart.getData().add(series);
    }

    private void setupListView() {
        pumpListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < dashboardViewModel.getClientIds().size()) {
                int id = dashboardViewModel.getClientIds().get(index);
                if (id != selectedClientId) {
                    selectedClientId = id;
                    rebuildChart(selectedClientId);
                    updateLabels(selectedClientId);
                }
            }
        });
        searchField.textProperty().addListener((obs, old, newVal) -> updateList(newVal));
    }

    private void updateList(String search) {
        List<String> items = new ArrayList<>();
        for (int id : dashboardViewModel.getClientIds()) {
            String name = dashboardViewModel.getClientNames().getOrDefault(id, "Pump " + id);
            if (search == null || search.isEmpty()
                    || name.toLowerCase().contains(search.toLowerCase())) {
                items.add(name + "  |  HP-00" + id);
            }
        }
        pumpListView.setItems(FXCollections.observableArrayList(items));
    }

    private void updateLabels(int clientId) {
        String name = dashboardViewModel.getClientNames().getOrDefault(clientId, "Pump " + clientId);
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
    }

    private void rebuildChart(int clientId) {
        List<Double> history = dashboardViewModel.getWaterFlowHistory().getOrDefault(clientId, new ArrayList<>());
        List<String> times = dashboardViewModel.getTimeHistory().getOrDefault(clientId, new ArrayList<>());

        chartData.clear();
        for (int i = 0; i < history.size(); i++) {
            chartData.add(new XYChart.Data<>(times.get(i), history.get(i)));
        }
        lastChartClientId = clientId;
    }

    private void addLatestPoint(int clientId) {
        List<Double> history = dashboardViewModel.getWaterFlowHistory().getOrDefault(clientId, new ArrayList<>());
        List<String> times = dashboardViewModel.getTimeHistory().getOrDefault(clientId, new ArrayList<>());

        if (!history.isEmpty()) {
            int last = history.size() - 1;
            chartData.add(new XYChart.Data<>(times.get(last), history.get(last)));
            if (chartData.size() > 10) {
                chartData.remove(0);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("sensorData")) {
            SensorDataDTO dto = (SensorDataDTO) evt.getNewValue();
            Platform.runLater(() -> {
                int id = dto.getClientId();

                if (!dashboardViewModel.getClientIds().contains(id)) {
                    dashboardViewModel.getClientIds().add(id);
                    dashboardViewModel.getClientNames().put(id, "Pump " + id);
                    dashboardViewModel.getWaterFlowHistory().put(id, new ArrayList<>());
                    dashboardViewModel.getTimeHistory().put(id, new ArrayList<>());
                    updateList(searchField.getText());
                }

                if (selectedClientId == -1) {
                    selectedClientId = id;
                    pumpListView.getSelectionModel().select(0);
                }

                if (id == selectedClientId) {
                    updateLabels(id);
                    addLatestPoint(id);
                }
            });
        }

        // Vis alarm i boksen nederst på dashboard
        if (evt.getPropertyName().equals("alarm")) {
            Platform.runLater(() -> {
                String raw = evt.getNewValue().toString();
                com.google.gson.JsonObject obj = new com.google.gson.JsonParser().parse(raw).getAsJsonObject();
                String type = obj.get("alarmType").getAsString();
                String clientId = obj.get("clientId").getAsString();
                String time = obj.get("timestamp").getAsString().substring(0, 19);
                alarmLabel.setText("⚠ " + type + " — Client " + clientId + " — " + time);
            });
        }
    }
    @FXML private void showDashboard() { viewHandler.openView(ViewScene.DASHBOARD); }

    @FXML private void showAlarmLog() {
        dashboardViewModel.removeListener(this);
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.ALARM_LOG);
    }

    @FXML private void showConfig() {
        dashboardViewModel.removeListener(this);
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.CONFIGURATION);
    }

    @FXML private void handleLogout() {
        dashboardViewModel.removeListener(this);
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.LOGIN);
    }

    @FXML
    private void handleAddPump() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Pump");
        dialog.setHeaderText("Enter pump name:");
        dialog.showAndWait().ifPresent(name -> {
            int newId = dashboardViewModel.getClientIds().size() + 1;
            dashboardViewModel.getClientIds().add(newId);
            dashboardViewModel.getClientNames().put(newId, name);
            dashboardViewModel.getWaterFlowHistory().put(newId, new ArrayList<>());
            dashboardViewModel.getTimeHistory().put(newId, new ArrayList<>());
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
                int id = dashboardViewModel.getClientIds().remove(index);
                dashboardViewModel.getClientNames().remove(id);
                dashboardViewModel.getWaterFlowHistory().remove(id);
                dashboardViewModel.getTimeHistory().remove(id);
                updateList(searchField.getText());
                selectedClientId = -1;
                lastChartClientId = -1;
                chartData.clear();
                pumpNameLabel.setText("Select a pump");
                pumpIdLabel.setText("");
            }
        });
    }
}