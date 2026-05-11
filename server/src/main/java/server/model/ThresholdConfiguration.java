package server.model;

public class ThresholdConfiguration {
    private double waterFlowMin;
    private double waterFlowMax;
    private double temperatureMin;
    private double temperatureMax;
    private double energyMax;
    private double COPMin;

    public ThresholdConfiguration(double waterFlowMin, double waterFlowMax,
                                  double temperatureMin, double temperatureMax,
                                  double energyMax, double COPMin) {
        this.waterFlowMin = waterFlowMin;
        this.waterFlowMax = waterFlowMax;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.energyMax = energyMax;
        this.COPMin = COPMin;
    }

    public boolean isWaterFlowNormal(double v) {
        return v >= waterFlowMin && v <= waterFlowMax;
    }

    public boolean isTemperatureNormal(double v) {
        return v >= temperatureMin && v <= temperatureMax;
    }

    public boolean isEnergyNormal(double v) {
        return v <= energyMax;
    }

    public boolean isCOPNormal(double v) {
        return v >= COPMin;
    }
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

    public double getCopMin() {
        return COPMin;
    }
}



