package at.mritter.dezsys05;

import at.mritter.dezsys05.msg.Message;
import at.mritter.dezsys05.msg.MessageType;
import at.mritter.dezsys05.net.LDAPConnector;
import at.mritter.dezsys05.net.Networking;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * The encryptor represents a client or service that is able to send and receive encrypted messages.
 * It uses ldap to publish/retrieve the public key. The public key is then being used to exchange the symmetric key.
 * All further messages are then encrypted and decrypted using the symmetric key.
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public abstract class Encryptor {

    public static final Logger LOG = LogManager.getLogger(Encryptor.class);

    private SecretKey symKey;
    private Networking socket;
    private LDAPConnector connector;

    /**
     * Creates a new connection to the ldap server
     *
     * @param ldapHost host of the ldap server
     * @param ldapUsername host of the ldap server
     * @param ldapPassword host of the ldap server
     * @param ldapGroup host of the ldap server
     */
    public Encryptor(String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup) {
        this.connector = new LDAPConnector(ldapHost, ldapUsername, ldapPassword, ldapGroup);
    }

    /**
     * Handles new incoming messages.
     * Distinguishes between the messages by checking the type of the message.
     *
     * @param message Incoming message
     */
    public abstract void handleMessage(Message message);

    /**
     * Encrypts and sends a new message.
     *
     * @param text message as cleartext that is going to be encrypted and sent
     */
    public void sendEncryptedMessage(String text) {

        if (this.symKey == null)
            throw new IllegalStateException("Unable to send encrypted message: symetric key is null");

        try {

            LOG.info("Sending encrypted message...");

            // use the symmetric key (AES) for encryption
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, this.symKey);

            // encrypt the message
            byte[] encrypted = cipher.doFinal((text).getBytes());

            // send the message to the service/client
            Message message = new Message(encrypted, MessageType.ENCRYPTED_MESSAGE);
            this.socket.write(message);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

    }

    /**
     * Decrypts and prints a encrypted message
     *
     * @param message message to decrypt and print
     */
    public void printDecryptedMessage(byte[] message) {
        try {
            // decrypt using AES symmetric key
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.symKey);

            // decrypt message and print
            byte[] decoded = cipher.doFinal(message);
            System.out.println(new String(decoded));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Send a message to the other network ressource (server or client) and then close connection
     */
    public void disconnect() {
        // notify other ressource (server or client) of closing the connection
        Message message = new Message("Close connection", MessageType.CLOSE_CONNECTION);
        this.socket.write(message);

        // close connection
        this.socket.disconnect();
        this.connector.disconnect();
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
