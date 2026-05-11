package shared.socket;

import com.google.gson.Gson;

public class JsonMessage {
    private MessageType type;
    private String payload;
    private String version;
    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
    public void setValues(MessageType type, String payload){
        this.type = type;
        this.payload = payload;
    }
    public String toJson(){
        return new Gson().toJson(this);
    }
    public static JsonMessage fromJson(String raw){
        return new Gson().fromJson(raw,JsonMessage.class);
    }

}
