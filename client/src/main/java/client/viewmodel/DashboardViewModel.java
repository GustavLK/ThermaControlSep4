package client.viewmodel;

import client.model.ModelHandler;
import shared.dto.SensorDataDTO;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel implements PropertyChangeListener {
    private PropertyChangeSupport support;
    private ModelHandler modelHandler;

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
            latestData.put(dto.getClientId(), dto);
            support.firePropertyChange("sensorData", null, dto);
        }
    }
}
