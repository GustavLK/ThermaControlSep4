package client.viewmodel;

import client.model.ModelHandler;
import shared.dto.SensorDataDTO;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel implements PropertyChangeListener {
    private PropertyChangeSupport support;
    private ModelHandler modelHandler;
    // Tilføj disse felter
    private List<Integer> clientIds = new ArrayList<>();
    private Map<Integer, String> clientNames = new HashMap<>();
    private Map<Integer, List<Double>> waterFlowHistory = new HashMap<>();
    private Map<Integer, List<String>> timeHistory = new HashMap<>();

    // Tilføj disse metoder
    public List<Integer> getClientIds() {
        return clientIds;
    }

    public Map<Integer, String> getClientNames() {
        return clientNames;
    }

    public Map<Integer, List<Double>> getWaterFlowHistory() {
        return waterFlowHistory;
    }

    public Map<Integer, List<String>> getTimeHistory() {
        return timeHistory;
    }

    // Latest data per clientId
    private Map<Integer, SensorDataDTO> latestData = new HashMap<>();

    public DashboardViewModel(ModelHandler modelHandler) {
        this.modelHandler = modelHandler;
        this.support = new PropertyChangeSupport(this);
        modelHandler.addListener(this);
    }

    public SensorDataDTO getLatestForClient(int clientId) {
        return latestData.get(clientId);
    }

    public List<SensorDataDTO> getAllLatestData() {
        return new ArrayList<>(latestData.values());
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("sensorData")) {
            SensorDataDTO dto = (SensorDataDTO) evt.getNewValue();
            int id = dto.getClientId();

            // Registrer ny pump hvis ikke set før
            if (!clientIds.contains(id)) {
                clientIds.add(id);
                clientNames.put(id, "Pump " + id);
                waterFlowHistory.put(id, new ArrayList<>());
                timeHistory.put(id, new ArrayList<>());
            }

            // Opdater historik
            List<Double> wf = waterFlowHistory.get(id);
            List<String> t = timeHistory.get(id);
            wf.add(dto.getWaterFlow());
            t.add(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (wf.size() > 10) {
                wf.remove(0);
                t.remove(0);
            }

            latestData.put(id, dto);
            support.firePropertyChange("sensorData", null, dto);
        }
    }
}