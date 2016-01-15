package at.mritter.dezsys05;

import org.apache.commons.cli.*;

/**
 * This class is used to parse the command line arguments
 *
 * @author Mathias Ritter
 */
public class CommandParser {


    public static LDAPConnector parseCommands(String[] args) throws ParseException {

        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();

        CommandLine line = parser.parse(options, args);

        String host = line.getOptionValue('h');
        String username = line.getOptionValue('u');
        String password = line.getOptionValue('p');

        return new LDAPConnector(host, username, password, "group.service1");

    }

    /**
     * Builds the command line options
     *
     * @return the command line options
     */
    private static Options getOptions() {

        Options options = new Options();


        Option host = Option.builder("h").argName("host").longOpt("host")
                .desc("The ip of the LDAP server").required().hasArg().build();

        Option user = Option.builder("u").argName("user").longOpt("user")
                .desc("The LDAP username").required().hasArg().build();

        Option password = Option.builder("p").argName("password").longOpt("password")
                .desc("The LDAP password").required().hasArg().build();


        options.addOption(host);
        options.addOption(user);
        options.addOption(password);

        return options;
    }


    /**
     * Creates a Unix like help page that will be shown if the user enters invalid parameters
     */
    public static void printHelp() {
        String header = "Client application to LDAP authentication and authorisation";
        String footer = "";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("dezsys05", header, getOptions(), footer, true);
    }

}
