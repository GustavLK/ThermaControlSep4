package server.model;

import shared.dto.AlarmType;
import shared.dto.SensorDataDTO;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface HeatPumpModel {
    void receiveData(SensorDataDTO dto);

    void evaluateThresholds(SensorDataDTO data);

    void triggerAlarm(AlarmType type, int clientId);

    void addListener(PropertyChangeListener listener);

    void removeListener(PropertyChangeListener listener);

    void fireEvent(String propertyName, Object value);

    List<HeatPumpClient> getClients();

    List<Alarm> getAlarmLog();
}