package at.mritter.dezsys05;


import at.mritter.dezsys05.msg.Message;
import at.mritter.dezsys05.msg.MessageType;
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

public class Client extends Encryptor {

    public static final Logger LOG = LogManager.getLogger(Client.class);
    private final Input input;

    private PublicKey publicKey;

    public Client(Input input, String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, String serviceHost, int servicePort) {

        this.input = input;

        super.setSymKey(generateSymKey());

        LDAPConnector connector = new LDAPConnector(ldapHost, ldapUsername, ldapPassword, ldapGroup);
        super.setConnector(connector);

        Networking socket = new SocketClient(this, serviceHost, servicePort);
        super.setSocket(socket);
        socket.connect();


    }

    public void fetchPublicKey() {
        try {
            // the LDAP-Key from the server
            String ldapKey = super.getConnector().getDescription();
            // convert hex to bytes and wrap it so java can create a public key
            byte[] key = hexStringToByteArray(ldapKey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
            // set the key algorithm
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private SecretKey generateSymKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendEncryptSymKey() {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            byte[] encoded = cipher.doFinal(super.getSymKey().getEncoded());

            Message message = new Message(encoded, MessageType.ENCRYPTED_SYM_KEY);

            super.getSocket().write(message);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

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
                break;
        }
    }


    private byte[] hexStringToByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
}
