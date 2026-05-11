package server.model;

import shared.dto.AlarmStatus;
import shared.dto.AlarmType;

import java.time.LocalDateTime;

public class Alarm {
    private AlarmType alarmType;
    private LocalDateTime timestamp;
    private AlarmStatus status;
    private int clientId;


    public Alarm(AlarmType alarmType, LocalDateTime timestamp, AlarmStatus status, int clientId) {
        this.alarmType = alarmType;
        this.timestamp = timestamp;
        this.status = status;
        this.clientId = clientId;
    }
    public Alarm(AlarmType alarmType, int clientId) {
        this.alarmType = alarmType;
        this.clientId = clientId;
        this.timestamp = LocalDateTime.now();
        this.status = AlarmStatus.ACTIVE;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public int getClientId() {
        return clientId;
    }
}

