package at.mritter.dezsys05.net;


import at.mritter.dezsys05.Display;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Networking implements Runnable {

    public static final Logger LOG = LogManager.getLogger(Networking.class);

    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean running = true;
    private List<Display> displays;

    protected Networking() {
        this.displays = new ArrayList<>();
    }

    public abstract void connect();

    public void write(byte[] message) {
        try {
            out.writeInt(message.length);
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        this.running = false;
        try {
            this.in.close();
            this.out.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    public void addObserver(Display display) {
        this.displays.add(display);
    }


    @Override
    public void run() {
        if (in == null || out == null)
            throw new IllegalStateException("Connection has not been established yet.");
        while (running){
            try {
                int length = in.readInt();
                byte[] message = new byte[length];
                in.readFully(message, 0, message.length); // read the message

                for (Display display : displays)
                    display.show(message);

            } catch (Exception e) {
                LOG.error(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }
}
