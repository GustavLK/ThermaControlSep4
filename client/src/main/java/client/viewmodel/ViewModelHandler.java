package client.viewmodel;

import client.model.ModelHandler;

public class ViewModelHandler {
    private ModelHandler modelHandler;
    private DashboardViewModel dashboardViewModel;
    private AlarmViewModel alarmViewModel;
    private ConfigViewModel configViewModel;
    private PerformanceViewModel performanceViewModel;

    public ViewModelHandler(ModelHandler modelHandler) {
        this.modelHandler = modelHandler;
        this.dashboardViewModel = new DashboardViewModel(modelHandler);
        this.alarmViewModel = new AlarmViewModel(modelHandler);
        this.configViewModel = new ConfigViewModel(modelHandler);
        this.performanceViewModel = new PerformanceViewModel(modelHandler);
    }

    public DashboardViewModel getDashboardViewModel() {
        return dashboardViewModel;
    }

    public AlarmViewModel getAlarmViewModel() {
        return alarmViewModel;
    }

    public ConfigViewModel getConfigViewModel() {
        return configViewModel;
    }

    public ModelHandler getModelHandler() {
        return modelHandler;
    }

    public PerformanceViewModel getPerformanceViewModel() {
        return performanceViewModel;
    }
}
