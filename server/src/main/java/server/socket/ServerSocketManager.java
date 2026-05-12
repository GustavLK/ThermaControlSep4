package server.socket;

import server.model.HeatPumpModelManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager implements Runnable {
    private ServerSocket serverSocket;
    private HeatPumpModelManager modelManager;
    private boolean running;

    public ServerSocketManager(int port, HeatPumpModelManager modelManager) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.modelManager = modelManager;
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        System.out.println("Waiting for clients...");

        while (running) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ServerClientHandler handler = new ServerClientHandler(socket, modelManager);
                new Thread(handler).start();

            } catch (IOException e) {
                System.out.println("Error accepting client");
            }
        }
    }
}