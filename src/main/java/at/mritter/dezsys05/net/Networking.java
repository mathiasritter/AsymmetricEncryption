package at.mritter.dezsys05.net;


import at.mritter.dezsys05.Display;

public interface Networking extends Runnable {

    void connect();

    void disconnect();

    void write(byte[] message);

    void addObserver(Display display);

}
