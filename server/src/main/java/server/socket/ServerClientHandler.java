package server.socket;

import server.model.HeatPumpModelManager;
import shared.dto.SensorDataDTO;

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

                SensorDataDTO dto = parseSensorData(message);

                modelManager.receiveData(dto);

                send("ACK");
            }

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    private SensorDataDTO parseSensorData(String message) {
        String[] parts = message.split(";");

        int clientId = 0;
        double temperature = 0;
        double waterFlow = 0;
        double COP = 3.2;

        for (String part : parts) {
            String[] keyValue = part.split("=");

            if (keyValue.length != 2) {
                continue;
            }

            if (keyValue[0].equals("clientId")) {
                clientId = Integer.parseInt(keyValue[1]);
            } else if (keyValue[0].equals("temperature")) {
                temperature = Double.parseDouble(keyValue[1]);
            } else if (keyValue[0].equals("waterFlow")) {
                waterFlow = Double.parseDouble(keyValue[1]);
            } else if (keyValue[0].equals("COP")) {
                COP = Double.parseDouble(keyValue[1]);
            }
        }

        return new SensorDataDTO(clientId, waterFlow, temperature, COP);
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}