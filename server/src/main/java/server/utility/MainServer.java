package server.utility;

import server.model.HeatPumpModelManager;
import server.socket.ServerSocketManager;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws IOException {
        HeatPumpModelManager modelManager = new HeatPumpModelManager();

        modelManager.addListener(event -> {
            System.out.println("MODEL EVENT: "
                    + event.getPropertyName()
                    + " -> "
                    + event.getNewValue());
        });

        ServerSocketManager server = new ServerSocketManager(8080, modelManager);
        new Thread(server).start();
    }
}
