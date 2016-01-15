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

public class SocketClient implements Networking {

    public static final Logger LOG = LogManager.getLogger(SocketClient.class);

    private List<Display> displays;

    private volatile boolean running = true;

    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private String host = "";
    private int port = 0;

    public SocketClient(String host, int port) {
        this.displays = new ArrayList<>();
        this.host = host;
        this.port = port;
    }


    @Override
    public void connect() {
        try {
            this.socket = new Socket(host, port);
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        this.running = false;
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Closed connection");
    }

    @Override
    public void write(byte[] message) {
        try {
            out.writeInt(message.length);
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addObserver(Display display) {
        this.displays.add(display);
    }

    @Override
    public void run() {
        try {
            int length = this.in.readInt();
            if (length > 0) {
                byte[] message = new byte[length];
                this.in.readFully(message, 0, message.length);
                for (Display display : this.displays)
                    display.show(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
