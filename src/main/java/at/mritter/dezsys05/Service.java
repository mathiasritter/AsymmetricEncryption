package at.mritter.dezsys05;

import at.mritter.dezsys05.msg.Message;
import at.mritter.dezsys05.msg.MessageType;
import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketServer;
import at.mritter.dezsys05.ui.Input;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;

/**
 * The service is going to communicate with the client using symmetric keys.
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class Service extends Encryptor {

    private KeyPair keyPair;

    private Input input;

    public static final Logger LOG = LogManager.getLogger(Service.class);

    /**
     * Connects to ldap server and creates the service.
     * Generates a public and private key for the clients.
     *
     * @param input user input
     * @param ldapHost host of ldap server
     * @param ldapUsername username of ldap server
     * @param ldapPassword password of ldap user
     * @param ldapGroup group of ldap user
     * @param servicePort port of service
     */
    public Service(Input input, String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, int servicePort) {

        super(ldapHost, ldapUsername, ldapPassword, ldapGroup);
        this.input = input;

        // generate public/private key
        this.keyPair = generateKeyPair();

        // create new server socket and accept clients
        Networking socket = new SocketServer(this, servicePort);
        super.setSocket(socket);
        socket.connect();

    }

    /**
     * Generates public/private key pair using RSA algorithm
     *
     * @return the generated key pair
     */
    private KeyPair generateKeyPair()  {
        try {

            // generate public and private key (keypair)
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024, random);
            return generator.generateKeyPair();

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }

        return null;
    }

    /**
     * Stores the public key on the ldap server using the ldap connector
     */
    private void storePublicKey() {
        String pubKey = DatatypeConverter.printHexBinary(this.keyPair.getPublic().getEncoded());
        super.getConnector().setGroupDescription(pubKey);
    }

    /**
     * Decrypt the encrypted symmetric key that is used for further communication
     *
     * @param message encrypted symmetric key
     */
    private void decryptSymKey(byte[] message) {
        try {

            // decrypt symmetric key using the private key
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(message);

            // set decrypted key that is used for further enryption
            super.setSymKey(new SecretKeySpec(decrypted, 0, decrypted.length, "AES"));

        } catch (Exception e) {
            LOG.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @see Encryptor#handleMessage(Message)
     */
    @Override
    public void handleMessage(Message message) {

        Message response;

        switch (message.getType()) {

            case ENCRYPTED_SYM_KEY:
                LOG.info("Received encrypted symmetric key, now decrypting...");
                this.decryptSymKey(message.getContent());
                response = new Message("Successfully decrypted symmetric key", MessageType.SERVICE_READY);
                super.getSocket().write(response);

                LOG.info("Ready for sending encrypted messages!");
                input.acceptUserInput(this);
                break;

            case CLIENT_CONNECTED:
                LOG.info("Client connected successfully, now storing public key...");
                this.storePublicKey();
                response = new Message("Successfully stored public Key", MessageType.STORED_PUB_KEY);
                super.getSocket().write(response);
                break;

            case ENCRYPTED_MESSAGE:
                LOG.info("Received an encrypted message, decrypting...");
                super.printDecryptedMessage(message.getContent());
                break;

            case CLOSE_CONNECTION:
                LOG.info("Client closed connection, exiting...");
                super.disconnect();
                System.exit(0);
                break;
        }
    }

}
