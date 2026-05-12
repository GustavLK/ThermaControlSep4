package server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager {
    private ServerSocket welcomeSocket;
    private ServerClientHandlerPool HANDLER_POOL;
    private int port;

    public ServerSocketManager(int port) throws IOException {
        this.port = port;
        this.welcomeSocket = new ServerSocket(port);
        this.HANDLER_POOL = new ServerClientHandlerPool();
    }
    public void run(){
        startListening();
    }
    public void startListening() {
        while (true) {
            try {
                Socket socket = welcomeSocket.accept();
                spawnClientHandler(socket);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void broadcast(String message){
        HANDLER_POOL.broadcast(message,null);
    }
    public void stopServer() {
        try {
            welcomeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void spawnClientHandler(Socket socket) throws IOException {
        ServerClientHandler handler = new ServerClientHandler(socket);
        HANDLER_POOL.add(handler);
        new Thread(handler).start();
    }
}
