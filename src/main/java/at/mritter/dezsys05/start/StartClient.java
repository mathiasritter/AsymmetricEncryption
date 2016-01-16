package at.mritter.dezsys05.start;

import at.mritter.dezsys05.Client;
import at.mritter.dezsys05.Service;
import at.mritter.dezsys05.ui.ConsoleInput;

/**
 * The main method of this class starts the client
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class StartClient {


    /**
     * Start client
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        Client client = new Client(new ConsoleInput(), Configuration.LDAP_HOST , Configuration.LDAP_USER,
                Configuration.LDAP_PASSWORD, Configuration.LDAP_GROUP, Configuration.SERVICE_HOST,
                Configuration.SERVICE_PORT);

    }


}
