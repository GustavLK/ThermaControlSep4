package server.utility;

import server.socket.ServerSocketManager;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws IOException {
        ServerSocketManager server = new ServerSocketManager(8080);
        new Thread(server::run).start();
    }
}
