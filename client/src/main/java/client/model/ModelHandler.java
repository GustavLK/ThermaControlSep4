package client.model;

import client.socket.ClientSocketManager;
import client.socket.HeatPumpSimulator;
import com.google.gson.Gson;
import shared.dto.SensorDataDTO;
import shared.socket.JsonMessage;
import shared.socket.MessageType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelHandler implements PropertyChangeListener {
    private ClientSocketManager socketManager;
    private HeatPumpSimulator simulator;
    private PropertyChangeSupport support;

    public ModelHandler() {
        support = new PropertyChangeSupport(this);

        socketManager = new ClientSocketManager("localhost", 8080);
        socketManager.addListener(this);

        simulator = new HeatPumpSimulator(socketManager);
    }

    public void startSimulation() {
        simulator.startSimulation();
    }

    public void stopSimulation() {
        simulator.stopSimulation();
    }

    public void sendSensorData(int clientId, double temperature, double waterFlow) {
        String message = "clientId=" + clientId
                + ";temperature=" + temperature
                + ";waterFlow=" + waterFlow;

        socketManager.sendData(message);
    }

    public void disconnect() {
        socketManager.disconnect();
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("messageReceived")) {
            String raw = (String) event.getNewValue();
            JsonMessage msg = JsonMessage.fromJson(raw);

            if (msg.getType() == MessageType.ACKNOWLEDGEMENT) {
                SensorDataDTO dto = new Gson().fromJson(msg.getPayload(), SensorDataDTO.class);
                support.firePropertyChange("sensorData", null, dto);
            } else if (msg.getType() == MessageType.ALARM_NOTIFICATION) {
                support.firePropertyChange("alarm", null, msg.getPayload());
            }
        }
    }
}