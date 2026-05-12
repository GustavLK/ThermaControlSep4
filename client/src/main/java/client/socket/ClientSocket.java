package client.socket;

import java.beans.PropertyChangeListener;

public interface ClientSocket {
    void connect(String host, int port);

    void disconnect();

    void sendData(String data);

    void addListener(PropertyChangeListener listener);

    void removeListener(PropertyChangeListener listener);
}