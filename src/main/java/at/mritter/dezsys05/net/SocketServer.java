package at.mritter.dezsys05.net;


import at.mritter.dezsys05.Display;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer implements Networking {

    public static final Logger LOG = LogManager.getLogger(SocketServer.class);

    private volatile boolean running = true;

    private List<Display> displays;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;


    public SocketServer(int port) {
        this.displays = new ArrayList<>();
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            LOG.info("Waiting for a client to connect...");
            clientSocket = serverSocket.accept();
            LOG.info("Accepted new connection");

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
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
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Closed connection");

    }

    @Override
    public void write(byte[] message) {
        throw new NotImplementedException();
    }

    @Override
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

                LOG.info("Received new message");

                for (Display display : displays)
                    display.show(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
