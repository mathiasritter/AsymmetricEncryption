package at.mritter.dezsys05;

import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class Service implements Display {

    private KeyPair keyPair;
    private SecretKey symKey;

    private LDAPConnector connector;

    private Networking socketServer;

    public static final Logger LOG = LogManager.getLogger(Service.class);

    public Service(String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, int servicePort) {

        try {
            // Generate the KeyPair for later use
            this.keyPair = generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        this.connector = new LDAPConnector(ldapHost, ldapUsername, ldapPassword, ldapGroup);

        this.socketServer = new SocketServer(servicePort);
        this.socketServer.addDisplay(this);
        this.socketServer.connect();

    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        generator.initialize(1024, random);
        return generator.generateKeyPair();
    }

    public void storePublicKey() {
        this.connector.setDescription(byteArrayToHexString(this.keyPair.getPublic().getEncoded()));
    }


    private void decryptSymKey(byte[] message) {
        try {
            System.out.println("Decrypting SymKey with private key...");

            // set decryption algorithm
            Cipher cipher = Cipher.getInstance("RSA");
            // decrypt and set the sym key
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(message);
            this.symKey = new SecretKeySpec(decrypted, 0, decrypted.length, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(byte[] message) {
        LOG.info("Service has received a new message");
        this.decryptSymKey(message);
    }

    public void sendEncryptedMessage(String message) {
        if (this.symKey != null) {
            try {
                System.out.println("Sending encrypted message to server: " + message);
                // Set the encryption algorithm
                Cipher cipher = Cipher.getInstance("AES");
                // use the sym key to encrypt
                cipher.init(Cipher.ENCRYPT_MODE, this.symKey);

                // encrypt the actual message
                byte[] encrypted = cipher.doFinal((message).getBytes());
                // send it to the client
                this.socketServer.write(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("SymKey is null");
        }
    }

    public void disconnect() {
        this.socketServer.disconnect();
        this.connector.disconnect();
    }

    private String byteArrayToHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }
}
