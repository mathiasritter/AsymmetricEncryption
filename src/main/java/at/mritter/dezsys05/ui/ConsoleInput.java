package at.mritter.dezsys05.ui;

import at.mritter.dezsys05.Encryptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Mathias on 16.01.16.
 */
public class ConsoleInput implements Input {

    public static final Logger LOG = LogManager.getLogger(ConsoleInput.class);

    @Override
    public void acceptUserInput(Encryptor sender) {

        new Thread(() -> {

            LOG.info("Please enter a message or \"exit\" to exit");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = null;
                try {
                    line = br.readLine();
                    if (line.equals("exit")) {
                        sender.disconnect();
                        break;
                    }
                    sender.sendEncryptedMessage(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }).start();





    }
}
