package at.mritter.dezsys05.start;

import at.mritter.dezsys05.Client;
import at.mritter.dezsys05.Service;
import at.mritter.dezsys05.ui.ConsoleInput;

/**
 * This class starts service and client
 *
 * @author Mathias Ritter
 */
public class StartService {

    public static void main(String[] args) throws InterruptedException {

        Service service = new Service(new ConsoleInput(), Configuration.LDAP_HOST , Configuration.LDAP_USER,
                Configuration.LDAP_PASSWORD, Configuration.LDAP_GROUP, Configuration.SERVICE_PORT);

    }


}
