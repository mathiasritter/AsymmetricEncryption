package at.mritter.dezsys05.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 16.01.16.
 */
public enum MessageType {

    ENCRYPTED_SYM_KEY('S'), ENCRYPTED_MESSAGE('E'), STORED_PUB_KEY('P'), CLIENT_READY('R'), CLOSE_CONNECTION('C');


    private final char value;
    private static Map<Character, MessageType> map = new HashMap<>();

    MessageType(char value) {
        this.value = value;
    }

    static {
        for (MessageType messageType : MessageType.values()) {
            map.put(messageType.value, messageType);
        }
    }

    public static MessageType valueOf(char messageType) {
        return map.get(messageType);
    }



    public char getValue() {
        return value;
    }
}
