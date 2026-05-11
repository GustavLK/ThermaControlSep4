package server.model;

public class HeatPumpClient {
    private int id;
    private String clientName;
    private ConnectionStatus connectionStatus;
    private ThresholdConfiguration threshold;

    public HeatPumpClient(int id, String name) {
        this.id = id;
        this.clientName = name;
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return clientName;
    }
    public ConnectionStatus getStatus(){
        return connectionStatus;
    }

    public ThresholdConfiguration getThreshold() {
        return threshold;
    }
    public void setStatus(ConnectionStatus status){
        this.connectionStatus = status;
    }

}
