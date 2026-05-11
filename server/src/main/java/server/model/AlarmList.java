package server.model;

import java.util.ArrayList;
import java.util.List;

public class AlarmList {
    private List<Alarm>alarms;

    public AlarmList(){
        alarms = new ArrayList<>();
    }

    public void addAlarm(Alarm alarm){
        alarms.add(alarm);
    }

    public List<Alarm> getAll() {
        return alarms;
    }
    public List<Alarm> getByClientId(int id) {
        List<Alarm> result = new ArrayList<>();

        for (Alarm alarm : alarms) {
            if (alarm.getClientId() == id) {
                result.add(alarm);
            }
        }

        return result;
    }
}
