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

public class SocketServer extends Networking {

    public static final Logger LOG = LogManager.getLogger(SocketServer.class);

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public SocketServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void connect() {
        try {
            LOG.info("Waiting for a client to connect...");
            clientSocket = serverSocket.accept();

            super.setIn(new DataInputStream(clientSocket.getInputStream()));
            super.setOut(new DataOutputStream(clientSocket.getOutputStream()));
            LOG.info("Accepted new connection");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

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
        LOG.info("Closed connection");

    }


}
