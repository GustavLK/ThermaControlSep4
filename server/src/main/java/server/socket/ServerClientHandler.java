package server.socket;

import shared.socket.JsonMessage;
import shared.socket.MessageType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InputStreamReader;

public class ServerClientHandler implements Runnable{
    private BufferedReader in;
    private PrintWriter out;
    private String CLIENT_ADDRESS;
    private PropertyChangeSupport SUPPORT;

    public ServerClientHandler(Socket socket) throws IOException {
        this.CLIENT_ADDRESS = socket.getInetAddress().getHostAddress();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.SUPPORT = new PropertyChangeSupport(this);
    }
    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                SUPPORT.firePropertyChange("message", null, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(String message) {
        out.println(message);
    }
    public void addListener(PropertyChangeListener listener){
        SUPPORT.addPropertyChangeListener(listener);
    }
    public void removeListener(PropertyChangeListener listener){
        SUPPORT.removePropertyChangeListener(listener);
    }
    public void sendAck(ServerClientHandler handler){
        JsonMessage ack = new JsonMessage();
        ack.setValues(MessageType.ACKNOWLEDGEMENT,"OK");
        handler.send(ack.toJson());
    }
}
