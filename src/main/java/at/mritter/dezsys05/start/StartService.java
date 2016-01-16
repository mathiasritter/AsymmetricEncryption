package at.mritter.dezsys05.start;

import at.mritter.dezsys05.Client;
import at.mritter.dezsys05.Service;
import at.mritter.dezsys05.ui.ConsoleInput;

/**
 * The main method of this class starts the service
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class StartService {

    /**
     * Start service
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        Service service = new Service(new ConsoleInput(), Configuration.LDAP_HOST , Configuration.LDAP_USER,
                Configuration.LDAP_PASSWORD, Configuration.LDAP_GROUP, Configuration.SERVICE_PORT);

    }


}
