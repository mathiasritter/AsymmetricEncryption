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

public class Service extends Encryptor implements Recipient {

    private KeyPair keyPair;

    private Input input;

    public static final Logger LOG = LogManager.getLogger(Service.class);

    public Service(Input input, String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, int servicePort) {

        this.input = input;

        try {
            // Generate the KeyPair for later use
            this.keyPair = generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        LDAPConnector connector = new LDAPConnector(ldapHost, ldapUsername, ldapPassword, ldapGroup);
        super.setConnector(connector);

        Networking socket = new SocketServer(servicePort);
        super.setSocket(socket);
        socket.addDisplay(this);
        socket.connect();

    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        generator.initialize(1024, random);
        return generator.generateKeyPair();
    }

    private void storePublicKey() {
        super.getConnector().setDescription(byteArrayToHexString(this.keyPair.getPublic().getEncoded()));
    }


    private void decryptSymKey(byte[] message) {
        try {
            // set decryption algorithm
            Cipher cipher = Cipher.getInstance("RSA");
            // decrypt and set the sym key
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(message);
            super.setSymKey(new SecretKeySpec(decrypted, 0, decrypted.length, "AES"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message message) {

        Message response = null;

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
                break;
        }
    }

    private String byteArrayToHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }
}
