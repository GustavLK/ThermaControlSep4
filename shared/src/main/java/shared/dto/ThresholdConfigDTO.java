package shared.dto;

public class ThresholdConfigDTO {
    private double waterFlowMin;
    private double waterFlowMax;
    private double temperatureMin;
    private double temperatureMax;
    private double energyMax;
    private double COPMin;

    public double getWaterFlowMin() {
        return waterFlowMin;
    }

    public double getWaterFlowMax() {
        return waterFlowMax;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public double getEnergyMax() {
        return energyMax;
    }

    public double getCOPMin() {
        return COPMin;
    }
}
