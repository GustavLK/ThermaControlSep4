package client.utility;

import client.socket.ClientSocketManager;
import client.socket.HeatPumpSimulator;

public class MainClient {
    public static void main(String[] args) {
        ClientSocketManager socketManager = new ClientSocketManager("localhost", 8080);

        HeatPumpSimulator simulator = new HeatPumpSimulator(socketManager);
        simulator.startSimulation();
    }
}