package server.socket;

import com.google.gson.Gson;
import server.model.HeatPumpModelManager;
import shared.dto.SensorDataDTO;
import shared.socket.JsonMessage;
import shared.socket.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private HeatPumpModelManager modelManager;

    public ServerClientHandler(Socket socket, HeatPumpModelManager modelManager) {
        this.socket = socket;
        this.modelManager = modelManager;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error creating streams");
        }
        // Tilføj i constructoren efter de andre linjer:
        modelManager.addListener(event -> {
            if (event.getPropertyName().equals("alarm")) {
                JsonMessage alarmMsg = new JsonMessage();
                alarmMsg.setValues(MessageType.ALARM_NOTIFICATION,
                        new Gson().toJson(event.getNewValue()));
                send(alarmMsg.toJson());
            }
        });
    }

    @Override
    public void run() {
        System.out.println("Handler started");

        try {
            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Server received: " + message);

                JsonMessage msg = JsonMessage.fromJson(message);
                SensorDataDTO dto = new Gson().fromJson(msg.getPayload(), SensorDataDTO.class);

                modelManager.receiveData(dto);

                JsonMessage ack = new JsonMessage();
                ack.setValues(MessageType.ACKNOWLEDGEMENT, msg.getPayload());
                send(ack.toJson());
            }

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}