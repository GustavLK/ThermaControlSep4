package client.socket;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketManager implements ClientSocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private PropertyChangeSupport support;
    private boolean running;

    public ClientSocketManager(String host, int port) {
        support = new PropertyChangeSupport(this);
        connect(host, port);
    }

    @Override
    public void connect(String host, int port) {
        if (socket != null) {
            disconnect();
        }

        try {
            socket = new Socket(host, port);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            out = new PrintWriter(socket.getOutputStream(), true);

            running = true;

            createReceiverThread().start();

            System.out.println("Client established connection with server");

        } catch (IOException e) {
            System.out.println("Error: Client failed to establish connection to server");
        }
    }

    @Override
    public void sendData(String data) {
        if (out != null) {
            out.println(data);
            System.out.println("Client sent: " + data);
        }
    }

    private Thread createReceiverThread() {
        return new Thread(() -> {
            try {
                String reply;

                while (running && (reply = in.readLine()) != null) {
                    System.out.println("Server replied: " + reply);

                    support.firePropertyChange("messageReceived", null, reply);
                }

            } catch (IOException e) {
                if (running) {
                    System.out.println("Error: Client lost connection to server");
                }
            }
        });
    }

    @Override
    public void disconnect() {
        try {
            running = false;

            if (socket != null) {
                socket.close();
                socket = null;
            }

            System.out.println("Client closed connection with server");

        } catch (IOException e) {
            System.out.println("Error: Client failed to close connection with server");
        }
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
