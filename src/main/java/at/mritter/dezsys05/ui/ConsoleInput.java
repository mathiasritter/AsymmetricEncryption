package at.mritter.dezsys05.ui;

import at.mritter.dezsys05.Encryptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is used for user inputs using the console
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public class ConsoleInput implements Input {

    public static final Logger LOG = LogManager.getLogger(ConsoleInput.class);

    /**
     * @see Input#acceptUserInput(Encryptor)
     */
    @Override
    public void acceptUserInput(Encryptor encryptor) {

        new Thread(() -> {

            LOG.info("Please enter a message or \"exit\" to exit");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            // read from the console till the user enters "exit"
            while (true) {
                String line = null;
                try {
                    line = br.readLine();
                    if (line.equals("exit")) {
                        encryptor.disconnect();
                        break;
                    }
                    encryptor.sendEncryptedMessage(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();


    }
}
