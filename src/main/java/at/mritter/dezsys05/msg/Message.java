package at.mritter.dezsys05.msg;

import java.nio.charset.StandardCharsets;

/**
 * This class represents a message that is used for the communication between client and service
 */
public class Message {

    private byte[] content;
    private MessageType type;

    public Message(byte[] content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public Message(String content, MessageType type) {
        this(content.getBytes(StandardCharsets.UTF_8), type);
    }

    public byte[] getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }


    public int getLength() {
        return content.length;
    }
}
