package at.mritter.dezsys05;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

public class ConsoleDisplay implements Display {

    public static final Logger LOG = LogManager.getLogger(ConsoleDisplay.class);

    @Override
    public void show(byte[] message) {
        try {
            String decoded = new String(message, "UTF-8");
            LOG.info(decoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
