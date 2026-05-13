package server.model;

import shared.dto.AlarmStatus;
import shared.dto.AlarmType;

public class Alarm {
    private AlarmType alarmType;
    private String timestamp; // String i stedet for LocalDateTime så Gson kan serialisere den
    private AlarmStatus status;
    private int clientId;

    public Alarm(AlarmType alarmType, AlarmStatus status, String timestamp, int clientId) {
        this.alarmType = alarmType;
        this.timestamp = timestamp;
        this.status = status;
        this.clientId = clientId;
    }

    public Alarm(AlarmType alarmType, int clientId) {
        this.alarmType = alarmType;
        this.clientId = clientId;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.status = AlarmStatus.ACTIVE;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public int getClientId() {
        return clientId;
    }
}