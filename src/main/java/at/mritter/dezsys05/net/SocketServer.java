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

/**
 * This class represents a network server.
 * The communication is ensued over sockets.
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class SocketServer extends Networking {

    public static final Logger LOG = LogManager.getLogger(SocketServer.class);

    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**
     * Creates a new server socket on the given port.
     *
     * @param port Port of the server socket that is created.
     */
    public SocketServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @see Networking#connect()
     */
    @Override
    public void connect() {
        new Thread(() -> {
            try {
                // accept new client connection, get streams to read/write
                clientSocket = serverSocket.accept();

                super.setIn(new DataInputStream(clientSocket.getInputStream()));
                super.setOut(new DataOutputStream(clientSocket.getOutputStream()));

                // start listening for incoming messages
                new Thread(this).start();
            } catch (Exception e) {
                LOG.error(e.getMessage());
                System.exit(-1);
            }
        }).start();

    }

    /**
     * @see Networking#disconnect()
     */
    @Override
    public void disconnect() {
        super.disconnect();
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }


}
