package at.mritter.dezsys05;

/**
 * Created by Mathias on 16.01.16.
 */
public class ConsoleInput implements Input {


    @Override
    public void acceptUserInput(Service service) {

        service.sendEncryptedMessage("Hello from the other side");

        service.disconnect();

    }
}
