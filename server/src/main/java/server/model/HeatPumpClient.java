package server.model;

public class HeatPumpClient {
    private int id;
    private String clientName;
    private ConnectionStatus connectionStatus;
    private ThresholdConfiguration threshold;

    public HeatPumpClient(int id, String clientName, ThresholdConfiguration threshold) {
        this.id = id;
        this.clientName = clientName;
        this.threshold = threshold;
        this.connectionStatus = ConnectionStatus.CONNECTED;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return clientName;
    }

    public ConnectionStatus getStatus() {
        return connectionStatus;
    }

    public ThresholdConfiguration getThreshold() {
        return threshold;
    }

    public void setStatus(ConnectionStatus status) {
        this.connectionStatus = status;
    }
}