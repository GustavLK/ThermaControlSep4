package server.model;

import java.util.ArrayList;
import java.util.List;

public class HeatPumpClientList {
    private int lastUsedId = -1;
    private List<HeatPumpClient> clients = new ArrayList<>();

    public HeatPumpClientList() {
    }

    public int addClient(String name) {
        lastUsedId++;
        HeatPumpClient client = new HeatPumpClient(lastUsedId, name);
        clients.add(client);
        return lastUsedId;
    }

    public void removeClient(int id) {
        for (HeatPumpClient client : clients) {
            if (client.getId() == id) {
                clients.remove(client);
                break;
            }
        }
    }

    public HeatPumpClient getClientById(int id) {
        for (HeatPumpClient client : clients)
            if (client.getId() == id) {
                return client;
            }
        return null;
    }
}

