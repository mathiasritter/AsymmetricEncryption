package at.mritter.dezsys05.net;


import at.mritter.dezsys05.Display;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient extends Networking {

    private Socket socket;

    private String host = "";
    private int port = 0;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() {
        try {
            this.socket = new Socket(host, port);
            super.setIn(new DataInputStream(this.socket.getInputStream()));
            super.setOut(new DataOutputStream(this.socket.getOutputStream()));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
        new Thread(this).start();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }





}
