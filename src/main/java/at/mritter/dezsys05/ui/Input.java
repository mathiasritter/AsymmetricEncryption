package at.mritter.dezsys05.ui;


import at.mritter.dezsys05.Encryptor;

/**
 * This interface is used for getting the user input (eg from the console or a gui)
 *
 * @author Mathias Ritter
 * @version 1.0
 */
public interface Input {

    /**
     * Handle user input
     *
     * @param encryptor The encryptor that is going to use the input from the user
     */
    void acceptUserInput(Encryptor encryptor);
}
