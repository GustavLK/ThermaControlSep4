package server.socket;

import java.util.ArrayList;
import java.util.List;

public class ServerClientHandlerPool {
    private List<ServerClientHandler> handlers;

    public ServerClientHandlerPool(){
        this.handlers = new ArrayList<ServerClientHandler>();
    }
    public void add(ServerClientHandler handler){
        handlers.add(handler);
    }
    public void broadcast(String message, ServerClientHandler source){ //
        for (ServerClientHandler handler : handlers) {
            if (handler != source) {
                handler.send(message);
            }
        }
    }
}
