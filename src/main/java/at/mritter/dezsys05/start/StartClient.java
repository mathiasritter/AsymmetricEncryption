package at.mritter.dezsys05.start;

import at.mritter.dezsys05.Client;
import at.mritter.dezsys05.Service;
import at.mritter.dezsys05.ui.ConsoleInput;

/**
 * This class starts service and client
 *
 * @author Mathias Ritter
 */
public class StartClient {


    public static void main(String[] args) throws InterruptedException {

        Client client = new Client(new ConsoleInput(), Configuration.LDAP_HOST , Configuration.LDAP_USER, Configuration.LDAP_PASSWORD,
                Configuration.LDAP_GROUP, Configuration.SERVICE_HOST, Configuration.SERVICE_PORT);

    }


}
