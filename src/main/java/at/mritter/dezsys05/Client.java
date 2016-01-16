package at.mritter.dezsys05;


import at.mritter.dezsys05.msg.Message;
import at.mritter.dezsys05.msg.MessageType;
import at.mritter.dezsys05.net.LDAPConnector;
import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketClient;
import at.mritter.dezsys05.ui.Input;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * The client is going to use a service and communicate with symmetric keys.
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class Client extends Encryptor {

    public static final Logger LOG = LogManager.getLogger(Client.class);
    private final Input input;

    private PublicKey publicKey;

    /**
     * Connects to ldap and the service.
     * Generates a symmetric key for encryption.
     *
     * @param input user input handler
     * @param ldapHost host of ldap server
     * @param ldapUsername username of ldap server
     * @param ldapPassword password of ldap user
     * @param ldapGroup group of ldap user
     * @param serviceHost host of service
     * @param servicePort port of service
     */
    public Client(Input input, String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, String serviceHost, int servicePort) {

        super(ldapHost, ldapUsername, ldapPassword, ldapGroup);

        this.input = input;
        super.setSymKey(generateSymKey());

        // connect to service
        Networking socket = new SocketClient(this, serviceHost, servicePort);
        super.setSocket(socket);
        socket.connect();

    }

    /**
     * Retrieve the public key from the ldap server
     */
    public void fetchPublicKey() {
        try {

            // the key is located in the description of the group
            byte[] key = DatatypeConverter.parseHexBinary(super.getConnector().getGroupDescription());

            // convert the string to public key, specify RSA algorithm
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(pubKeySpec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

    }

    /**
     * Generate a new symmetric key
     *
     * @return the generated symmetric key
     */
    private SecretKey generateSymKey() {
        try {
            // generate symmetric key using AES algorithm
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    /**
     * Send the symmetric key to the service, encrypted with the service's public key.
     */
    public void sendEncryptSymKey() {
        try {

            // encrypt symmetric key using the public key of the service
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            byte[] encoded = cipher.doFinal(super.getSymKey().getEncoded());

            // send encrypted symmetric key
            Message message = new Message(encoded, MessageType.ENCRYPTED_SYM_KEY);
            super.getSocket().write(message);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @see Encryptor#handleMessage(Message)
     */
    @Override
    public void handleMessage(Message message) {

        switch (message.getType()) {

            case STORED_PUB_KEY:
                LOG.info("Fetching public key from LDAP...");
                this.fetchPublicKey();
                LOG.info("Sending encrypted symmetric key to service...");
                this.sendEncryptSymKey();
                break;

            case ENCRYPTED_MESSAGE:
                LOG.info("Received an encrypted message, decrypting...");
                super.printDecryptedMessage(message.getContent());
                break;

            case SERVICE_READY:
                LOG.info("Ready for sending encrypted messages!");
                this.input.acceptUserInput(this);
                break;

            case CLOSE_CONNECTION:
                LOG.info("Server closed connection");
                super.disconnect();
                System.exit(0);
                break;
        }
    }

}
