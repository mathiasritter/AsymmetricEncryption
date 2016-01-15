package at.mritter.dezsys05;


import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Client implements Display {

    public static final Logger LOG = LogManager.getLogger(Client.class);

    private Networking socketClient;
    private LDAPConnector connector;


    private SecretKey symKey;
    private PublicKey publicKey;

    public Client(String ldapHost, String ldapUsername, String ldapPassword, String ldapGroup, String serviceHost, int servicePort) {

        this.symKey = generateSymKey();

        this.connector = new LDAPConnector(ldapHost, ldapUsername, ldapPassword, ldapGroup);

        this.socketClient = new SocketClient(serviceHost, servicePort);
        this.socketClient.addDisplay(this);
        this.socketClient.connect();

    }

    public void fetchPublicKey() {
        try {
            // the LDAP-Key from the server
            String ldapKey = this.connector.getDescription();
            // convert hex to bytes and wrap it so java can create a public key
            byte[] key = hexStringToByteArray(ldapKey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
            // set the key algorithm
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(pubKeySpec);
            LOG.debug("blaaaaaaa");
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
            byte[] encoded = cipher.doFinal(this.symKey.getEncoded());
            this.socketClient.write(encoded);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private void printDecryptedMessage(byte[] message) {
        try {
            // the algorithm to decrypt
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.symKey);
            // decode the data read from the server
            byte[] decoded = cipher.doFinal(message);
            System.out.println("Received encrypted message and decrypted it: " + new String(decoded));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(byte[] message) {
        LOG.info("Client has received a new message");
        this.printDecryptedMessage(message);
    }

    public void disconnect() {
        this.socketClient.disconnect();
        this.connector.disconnect();
    }

    private byte[] hexStringToByteArray(String s) {
        System.out.println("Converting byte array to hex: " + s);
        return DatatypeConverter.parseHexBinary(s);
    }
}
