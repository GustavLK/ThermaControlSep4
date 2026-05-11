package shared.dto;

public class SensorDataDTO {
    private int clientId;
    private String timestamp;
    private double waterFlow;
    private double energyConsumption;
    private double temperature;
    private double COP;


    public SensorDataDTO(int clientId, double waterFlow, double temperature, double COP){
        this.clientId = clientId;
        this.waterFlow = waterFlow;
        this.temperature = temperature;
        this.COP = COP;
    }

    public int getClientId() {
        return clientId;
    }
    public double getWaterFlow(){
        return waterFlow;
    }
    public double getEnergyConsumption(){
        return energyConsumption;
    }
    public double getTemperature(){
        return temperature;
    }
    public double getCOP(){
        return COP;
    }
    public boolean isValid() {
        return waterFlow >= 0
        && temperature > -273
        && energyConsumption >= 0
        && COP > 0;
    }
}
