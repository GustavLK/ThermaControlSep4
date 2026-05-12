package client.socket;

import com.google.gson.Gson;
import shared.dto.SensorDataDTO;
import shared.socket.JsonMessage;
import shared.socket.MessageType;

public class HeatPumpSimulator {
    private ClientSocketManager socketManager;
    private boolean running;

    public HeatPumpSimulator(ClientSocketManager socketManager) {
        this.socketManager = socketManager;
        this.running = false;
    }

    public void startSimulation() {
        running = true;

        Thread simulationThread = new Thread(() -> {
            int clientId = 1;

            while (running) {
                double temperature = generateBrineTemperature();
                double waterFlow = generateWaterFlow();
                double COP = generateCOP();

                SensorDataDTO dto = new SensorDataDTO(clientId, waterFlow, temperature, COP);
                JsonMessage msg = new JsonMessage();
                msg.setValues(MessageType.SENSOR_DATA_REQUEST, new Gson().toJson(dto));
                socketManager.sendData(msg.toJson());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        });

        simulationThread.start();
    }

    public void stopSimulation() {
        running = false;
    }

    private double generateBrineTemperature() {
        // long-term ca. 0.99 °C, winter ca. -1.19 °C, peak ca. -2.50 °C
        double min = -2.8;
        double max = 1.5;

        return round(min + Math.random() * (max - min));
    }

    private double generateWaterFlow() {
        // Estimeret flow pr. varmepumpe.
        // Hvis enheden tolkes som L/min, er ca. 3-28 L/min realistisk
        // baseret på winter load ca. 0.9-8.1 kW og dT = 3 °C.
        double min = 3.0;
        double max = 28.0;

        return round(min + Math.random() * (max - min));
    }

    private double generateCOP() {
        // Realistisk COP omkring jeres winter COP ca. 3.4
        // Nogle værdier kan komme under 2.5, så alarm kan testes.
        double min = 2.2;
        double max = 4.0;

        return round(min + Math.random() * (max - min));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}