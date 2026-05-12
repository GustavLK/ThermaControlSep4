package server.model;

import java.util.ArrayList;
import java.util.List;

public class HeatPumpClientList {
    private int lastUsedId = 0;
    private List<HeatPumpClient> clients = new ArrayList<>();

    public HeatPumpClientList() {
    }

    public int addClient(String name, ThresholdConfiguration threshold) {
        lastUsedId++;

        HeatPumpClient client = new HeatPumpClient(lastUsedId, name, threshold);

        clients.add(client);

        return lastUsedId;
    }

    public void removeClient(int id) {
        clients.removeIf(client -> client.getId() == id);
    }

    public HeatPumpClient getClientById(int id) {
        for (HeatPumpClient client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }

        return null;
    }

    public List<HeatPumpClient> getAll() {
        return clients;
    }
}