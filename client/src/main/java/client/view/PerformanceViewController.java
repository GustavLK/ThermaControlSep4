package client.view;

import client.viewmodel.PerformanceViewModel;
import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import shared.dto.SensorDataDTO;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class PerformanceViewController implements BaseViewController, PropertyChangeListener {

    @FXML private TableView<SensorDataDTO> performanceTable;
    @FXML private TableColumn<SensorDataDTO, String> idCol;
    @FXML private TableColumn<SensorDataDTO, String> tempCol;
    @FXML private TableColumn<SensorDataDTO, String> forbrugCol;
    @FXML private TableColumn<SensorDataDTO, String> copCol;
    @FXML private TableColumn<SensorDataDTO, String> prisCol;
    @FXML private TableColumn<SensorDataDTO, String> statusCol;
    @FXML private TextField elPrisField;
    @FXML private TextField filterField;
    @FXML private Label totalForbrugLabel;
    @FXML private Label totalPrisLabel;
    @FXML private Label avgTempLabel;
    @FXML private Label aktivePumperLabel;

    private ViewHandler viewHandler;
    private PerformanceViewModel performanceViewModel;
    private ObservableList<SensorDataDTO> tableData = FXCollections.observableArrayList();

    @Override
    public void init(ViewHandler viewHandler, ViewModelHandler viewModelHandler) {
        this.viewHandler = viewHandler;
        this.performanceViewModel = viewModelHandler.getPerformanceViewModel();
        performanceViewModel.addListener(this);

        setupTable();
        setupFilter();

        // Vis eksisterende data hvis der allerede er noget
        tableData.setAll(performanceViewModel.getAllLatestData());
        updateSummary();
    }

    private void setupTable() {
        idCol.setCellValueFactory(row ->
                new SimpleStringProperty(String.valueOf(row.getValue().getClientId())));

        tempCol.setCellValueFactory(row ->
                new SimpleStringProperty(row.getValue().getTemperature() + " °C"));

        forbrugCol.setCellValueFactory(row -> {
            double kw = beregnForbrug(row.getValue());
            return new SimpleStringProperty(String.format("%.2f kW", kw));
        });

        copCol.setCellValueFactory(row ->
                new SimpleStringProperty(String.valueOf(row.getValue().getCOP())));

        prisCol.setCellValueFactory(row -> {
            double kw = beregnForbrug(row.getValue());
            double elpris = parseElpris();
            double pris = kw * elpris;
            return new SimpleStringProperty(String.format("%.2f DKK", pris));
        });

        statusCol.setCellValueFactory(row -> {
            double cop = row.getValue().getCOP();
            return new SimpleStringProperty(cop < 2.5 ? "⚠ Lav COP" : "✓ Normal");
        });

        performanceTable.setItems(tableData);
    }

    private void setupFilter() {
        filterField.textProperty().addListener((obs, old, newVal) -> {
            List<SensorDataDTO> all = performanceViewModel.getAllLatestData();
            if (newVal == null || newVal.isEmpty()) {
                tableData.setAll(all);
            } else {
                tableData.setAll(all.stream()
                        .filter(dto -> String.valueOf(dto.getClientId()).contains(newVal))
                        .toList());
            }
            updateSummary();
        });

        // Opdater priser når elpris ændres
        elPrisField.textProperty().addListener((obs, old, newVal) -> {
            performanceTable.refresh();
            updateSummary();
        });
    }

    private double beregnForbrug(SensorDataDTO dto) {
        // Strømforbrug = varmeeffekt / COP
        // Varmeeffekt fra waterFlow, dT=3°C, brine densitet og varmekapacitet
        double heatPower = dto.getWaterFlow() * 1020 * 3900 * 3.0 / 60000;
        return heatPower / dto.getCOP() / 1000; // konvertér W til kW
    }

    private double parseElpris() {
        try {
            return Double.parseDouble(elPrisField.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            return 3.0; // default
        }
    }

    private void updateSummary() {
        List<SensorDataDTO> data = tableData;
        if (data.isEmpty()) return;

        double totalForbrug = data.stream().mapToDouble(this::beregnForbrug).sum();
        double totalPris = totalForbrug * parseElpris();
        double avgTemp = data.stream().mapToDouble(SensorDataDTO::getTemperature).average().orElse(0);

        totalForbrugLabel.setText(String.format("%.2f kW", totalForbrug));
        totalPrisLabel.setText(String.format("%.2f DKK", totalPris));
        avgTempLabel.setText(String.format("%.1f °C", avgTemp));
        aktivePumperLabel.setText(String.valueOf(data.size()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("sensorData")) {
            Platform.runLater(() -> {
                tableData.setAll(performanceViewModel.getAllLatestData());
                performanceTable.refresh();
                updateSummary();
            });
        }
    }

    @FXML private void showDashboard() {
        performanceViewModel.removeListener(this);
        viewHandler.openView(ViewScene.DASHBOARD);
    }

    @FXML private void showAlarmLog() {
        performanceViewModel.removeListener(this);
        viewHandler.openView(ViewScene.ALARM_LOG);
    }

    @FXML private void showConfig() {
        performanceViewModel.removeListener(this);
        viewHandler.openView(ViewScene.CONFIGURATION);
    }

    @FXML private void showPerformance() {
        viewHandler.openView(ViewScene.PERFORMANCE);
    }

    @FXML private void handleLogout() {
        performanceViewModel.removeListener(this);
        viewHandler.openView(ViewScene.LOGIN);
    }
}