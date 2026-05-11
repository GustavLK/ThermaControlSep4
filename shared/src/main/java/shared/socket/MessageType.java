package shared.socket;

public enum MessageType {
    SENSOR_DATA_REQUEST,
    SENSOR_DATA_RESPONSE,
    ACKNOWLEDGEMENT,
    ERROR,
    BROADCAST,
    ALARM_NOTIFICATION
}
