package client.viewmodel;

import client.model.ModelHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class AlarmViewModel implements PropertyChangeListener {
    private PropertyChangeSupport support;
    private ModelHandler modelHandler;
    private List<String> alarmLog = new ArrayList<>();

    public AlarmViewModel(ModelHandler modelHandler) {
        this.modelHandler = modelHandler;
        this.support = new PropertyChangeSupport(this);
        modelHandler.addListener(this);
    }

    public List<String> getAlarmLog() {
        return new ArrayList<>(alarmLog);
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("alarm")) {
            alarmLog.add(evt.getNewValue().toString());
            support.firePropertyChange("alarm", null, evt.getNewValue());
        }
    }

}
