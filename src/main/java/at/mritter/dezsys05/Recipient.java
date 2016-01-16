package at.mritter.dezsys05;


import at.mritter.dezsys05.msg.Message;

public interface Recipient {

    void handleMessage(Message message);

}
