package shared.dto;
public class AlarmDTO {
    private AlarmType alarmType;
    private String timestamp;
    private AlarmStatus status;
    private int clientId;

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getClientId() {
        return clientId;
    }
}
