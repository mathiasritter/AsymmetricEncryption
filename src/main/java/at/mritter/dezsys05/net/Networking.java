package at.mritter.dezsys05.net;


import at.mritter.dezsys05.Display;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a network resource that is able to read (receive) and write (send) messages
 * over the network.
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public abstract class Networking implements Runnable {

    public static final Logger LOG = LogManager.getLogger(Networking.class);

    private DataInputStream in;
    private DataOutputStream out;

    private volatile boolean running = true;

    private List<Display> displays;

    /**
     * Initialize array list of displays in constructor
     */
    protected Networking() {
        this.displays = new ArrayList<>();
    }

    /**
     * Connects to another network resource or waits for incoming connections. <br>
     * After the connection has been established, the thread is started. This means
     * that the network resource waits for incoming messages.
     */
    public abstract void connect();

    /**
     * Sends a new message to the connected network resource.
     *
     * @param message The message that should be sent
     */
    public void write(Message message) {
        try {
            out.writeChar(message.getType().getValue());
            out.writeInt(message.getLength());
            out.write(message.getContent());
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Disconnects from the connected network resource.
     * Closes io streams.
     */
    public void disconnect() {
        // set running to false so the thread stops
        this.running = false;
        try {
            // close io streams
            this.in.close();
            this.out.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Add a new display.
     * The displays are used to handleMessage the messages.
     *
     * @param display display to handleMessage the messages
     */
    public void addDisplay(Display display) {
        this.displays.add(display);
    }


    /**
     * Receives new messages while thread is running
     */
    @Override
    public void run() {

        if (in == null || out == null)
            throw new IllegalStateException("Unable to listen for incoming messages: Connection has not been established yet.");

        // while running is true listen for new messages
        while (running){
            try {

                MessageType messageType = MessageType.valueOf(in.readChar());
                int length = in.readInt();
                byte[] messageContent = new byte[length];
                in.readFully(messageContent, 0, messageContent.length);

                Message message = new Message(messageContent, messageType);

                // the attached displays handleMessage the message
                for (Display display : displays) {
                    display.handleMessage(message);
                }


            } catch (Exception e) {
                if (!running)
                    System.exit(0);
                else {
                    e.printStackTrace();
                    System.exit(-1);
                }
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
