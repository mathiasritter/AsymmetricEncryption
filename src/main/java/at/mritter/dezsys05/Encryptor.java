package at.mritter.dezsys05;

import at.mritter.dezsys05.msg.Message;
import at.mritter.dezsys05.msg.MessageType;
import at.mritter.dezsys05.net.Networking;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mathias on 16.01.16.
 */
public abstract class Encryptor {

    public static final Logger LOG = LogManager.getLogger(Encryptor.class);

    private SecretKey symKey;
    private Networking socket;
    private LDAPConnector connector;


    public void sendEncryptedMessage(String text) {

        if (this.symKey == null)
            throw new IllegalStateException("Unable to send encrypted message: symetric key is null");

        try {
            LOG.info("Sending encrypted message...");
            // Set the encryption algorithm
            Cipher cipher = Cipher.getInstance("AES");
            // use the sym key to encrypt
            cipher.init(Cipher.ENCRYPT_MODE, this.symKey);

            // encrypt the actual message
            byte[] encrypted = cipher.doFinal((text).getBytes());

            // send it to the client
            Message message = new Message(encrypted, MessageType.ENCRYPTED_MESSAGE);
            this.socket.write(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void printDecryptedMessage(byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.symKey);
            byte[] decoded = cipher.doFinal(message);
            System.out.println(new String(decoded));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Message message = new Message("Close connection", MessageType.CLOSE_CONNECTION);
        this.socket.write(message);
        this.socket.disconnect();
        this.connector.disconnect();
        System.exit(0);
    }


    public SecretKey getSymKey() {
        return symKey;
    }

    public void setSymKey(SecretKey symKey) {
        this.symKey = symKey;
    }

    public Networking getSocket() {
        return socket;
    }

    public void setSocket(Networking socket) {
        this.socket = socket;
    }

    public void setConnector(LDAPConnector connector) {
        this.connector = connector;
    }

    public LDAPConnector getConnector() {
        return connector;
    }
}
