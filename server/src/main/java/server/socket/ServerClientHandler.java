package server.socket;

import com.google.gson.Gson;
import server.model.HeatPumpModelManager;
import shared.dto.SensorDataDTO;
import shared.socket.JsonMessage;

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

                send("ACK");
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