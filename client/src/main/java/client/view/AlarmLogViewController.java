package client.view;

import client.viewmodel.AlarmViewModel;
import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlarmLogViewController implements BaseViewController, PropertyChangeListener {

    @FXML private TableView<AlarmRow> alarmTable;
    @FXML private TableColumn<AlarmRow, String> timeColumn;
    @FXML private TableColumn<AlarmRow, String> clientColumn;
    @FXML private TableColumn<AlarmRow, String> typeColumn;
    @FXML private TableColumn<AlarmRow, String> statusColumn;
    @FXML private TextField filterField;

    private ViewHandler viewHandler;
    private AlarmViewModel alarmViewModel;
    private ObservableList<AlarmRow> alarmRows = FXCollections.observableArrayList();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class AlarmRow {
        public String time, clientId, type, status;
        public AlarmRow(String time, String clientId, String type, String status) {
            this.time = time;
            this.clientId = clientId;
            this.type = type;
            this.status = status;
        }
    }

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
        this.alarmViewModel = viewModelHandler.getAlarmViewModel();
        alarmViewModel.addListener(this);

        timeColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().time));
        clientColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().clientId));
        typeColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().type));
        statusColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().status));

        alarmTable.setStyle("-fx-background-color: #111827; -fx-text-fill: #e8f4fd;");
        alarmTable.setItems(alarmRows);

        filterField.textProperty().addListener((obs, old, newVal) -> filterAlarms(newVal));

        for (String alarm : alarmViewModel.getAlarmLog()) {
            com.google.gson.JsonObject obj = new com.google.gson.JsonParser().parse(alarm).getAsJsonObject();
            String type = obj.get("alarmType").getAsString();
            String clientId = obj.get("clientId").getAsString();
            alarmRows.add(0, new AlarmRow(LocalDateTime.now().format(FMT), clientId, type, "ACTIVE"));
        }
    }

    private void filterAlarms(String filter) {
        if (filter == null || filter.isEmpty()) {
            alarmTable.setItems(alarmRows);
        } else {
            ObservableList<AlarmRow> filtered = FXCollections.observableArrayList();
            for (AlarmRow row : alarmRows) {
                if (row.type.toLowerCase().contains(filter.toLowerCase())
                        || row.clientId.contains(filter)) {
                    filtered.add(row);
                }
            }
            alarmTable.setItems(filtered);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("alarm")) {
            Platform.runLater(() -> {
                String raw = evt.getNewValue().toString();
                com.google.gson.JsonObject obj = new com.google.gson.JsonParser().parse(raw).getAsJsonObject();
                String type = obj.get("alarmType").getAsString();
                String clientId = obj.get("clientId").getAsString();
                alarmRows.add(0, new AlarmRow(LocalDateTime.now().format(FMT), clientId, type, "ACTIVE"));
            });
        }
    }
    @FXML private void showDashboard() {
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.DASHBOARD);
    }

    @FXML private void showAlarmLog() {
        viewHandler.openView(ViewScene.ALARM_LOG);
    }

    @FXML private void showConfig() {
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.CONFIGURATION);
    }

    @FXML private void handleLogout() {
        alarmViewModel.removeListener(this);
        viewHandler.openView(ViewScene.LOGIN);
    }
    @FXML private void showPerformance() {
        viewHandler.openView(ViewScene.PERFORMANCE);
    }
}