package at.mritter.dezsys05;


import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketClient;

import java.io.UnsupportedEncodingException;

public class StartClient {

    public static void main(String[] args) {

        Networking client = new SocketClient("127.0.0.1", 48263);

        Display out = new ConsoleDisplay();
        client.addObserver(out);

        client.connect();

        new Thread(client).start();

        String message = "Hallo";
        byte[] bytes = null;

        try {
             bytes = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.write(bytes);

    }

}
