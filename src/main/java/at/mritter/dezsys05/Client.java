package at.mritter.dezsys05;


import at.mritter.dezsys05.net.Networking;
import at.mritter.dezsys05.net.SocketClient;

public class Client {

    private Networking socketClient;
    private LDAPConnector connector;

    private static final String LDAP_HOST = "10.0.107.4";

    public Client() {

        this.connector = new LDAPConnector(LDAP_HOST, "admin", "user");
        this.socketClient = new SocketClient("127.0.0.1", 43987);

    }
}
