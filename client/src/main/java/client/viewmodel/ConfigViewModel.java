package client.viewmodel;

import client.model.ModelHandler;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ConfigViewModel {
    private PropertyChangeSupport support;
    private ModelHandler modelHandler;

    public ConfigViewModel(ModelHandler modelHandler) {
        this.modelHandler = modelHandler;
        this.support = new PropertyChangeSupport(this);
    }

    public void addClient(String name) {
        // Will be expanded when server supports it
        support.firePropertyChange("clientAdded", null, name);
    }

    public void removeClient(int id) {
        support.firePropertyChange("clientRemoved", null, id);
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
