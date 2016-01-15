package at.mritter.dezsys05;


import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketServer;

public class StartServer {

    public static void main(String[] args) {

        Networking server = new SocketServer(48263);

        Display out = new ConsoleDisplay();
        server.addObserver(out);

        server.connect();

        new Thread(server).start();

    }

}
