package at.mritter.dezsys05;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class parses the command line arguments and processes the user inputs
 *
 * @author Mathias Ritter
 */
public class Main {

    /**
     * Starts the program
     *
     * Command Line Arguments:
     * h: Hostname
     * u: Username
     * p: Passwort
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        /*
        try {
            LDAPConnector ldap = CommandParser.parseCommands(args);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Please enter a group or \"exit\" to exit");
                String line = br.readLine();
                if (line.equals("exit"))
                    break;
                ldap.userInGroup(line);
            }
            ldap.disconnect();

        } catch (ParseException e) {
            CommandParser.printHelp();
        } catch (IOException e) {
            System.out.println("Unable to read from console");
            e.printStackTrace();
        }
        */


    }


}
