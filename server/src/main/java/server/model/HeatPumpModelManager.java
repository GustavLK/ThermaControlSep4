package server.model;

import java.util.List;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import shared.dto.AlarmType;
import shared.dto.SensorDataDTO;


public class HeatPumpModelManager implements HeatPumpModel {
    private HeatPumpClientList list;
    private AlarmList alarms;
    private PropertyChangeSupport support;

    public HeatPumpModelManager() {
        list = new HeatPumpClientList();
        alarms = new AlarmList();
        support = new PropertyChangeSupport(this);
    }

    @Override
    public void receiveData(SensorDataDTO dto) {
        evaluateThresholds(dto);

        fireEvent("sensorData", dto);
    }

    @Override
    public void evaluateThresholds(SensorDataDTO data) {
        HeatPumpClient client = list.getClientById(data.getClientId());

        if (client == null) {
            triggerAlarm(AlarmType.CLIENT_OFFLINE, data.getClientId());
            return;
        }

        ThresholdConfiguration threshold = client.getThreshold();

        if (data.getWaterFlow() < threshold.getWaterFlowMin()) {
            triggerAlarm(AlarmType.WATER_FLOW_LOW, data.getClientId());
        }

        if (data.getWaterFlow() > threshold.getWaterFlowMax()) {
            triggerAlarm(AlarmType.WATER_FLOW_HIGH, data.getClientId());
        }

        if (data.getTemperature() < threshold.getTemperatureMin()) {
            triggerAlarm(AlarmType.TEMPERATURE_TOO_LOW, data.getClientId());
        }

        if (data.getTemperature() > threshold.getTemperatureMax()) {
            triggerAlarm(AlarmType.TEMPERATURE_TOO_HIGH, data.getClientId());
        }



        if (data.getCOP() < threshold.getCopMin()) {
            triggerAlarm(AlarmType.COP_BELOW_MIN, data.getClientId());
        }
    }

    @Override
    public void triggerAlarm(AlarmType type, int clientId) {
        Alarm alarm = new Alarm(type, clientId);

        alarms.addAlarm(alarm);

        fireEvent("alarm", alarm);

        broadcastAlarm(alarm);
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void fireEvent(String propertyName, Object value) {
        support.firePropertyChange(propertyName, null, value);
    }

    private void broadcastAlarm(Alarm alarm) {
        System.out.println("Ny alarm: " + alarm.getAlarmType()
                + " fra clientId: " + alarm.getClientId());
    }

    @Override
    public List<HeatPumpClient> getClients() {
        return list.getAll();
    }

    @Override
    public List<Alarm> getAlarmLog() {
        return alarms.getAll();
    }

    public void addClient(String name, ThresholdConfiguration threshold) {
        list.addClient(name, threshold);
    }
}