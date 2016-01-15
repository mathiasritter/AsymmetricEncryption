package at.mritter.dezsys05;


import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * This class is used to connect to the ldap service and execute commands
 *
 * @author Mathias Ritter
 */
public class LDAPConnector {

    private DirContext context;
    private String username;

    public LDAPConnector(String ip, String username, String password) {
        this.username = username;
        this.connect(ip, password);
    }

    /**
     * This method is used to connect to the ldap server
     *
     * @param ip server ip
     * @param password password of the user account
     */
    private void connect(String ip, String password) {


        Hashtable<String, Object> env = new Hashtable<>(11);

        // configure connection settings
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ip);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + this.username + ",dc=nodomain,dc=com");
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // establish connection
            this.context = new InitialDirContext(env);
            System.out.println("Authentication OK");
        } catch (NamingException e) {
            System.out.println(e.getExplanation());
        }

    }

    /**
     * This method is used to check whether the current user is in the given group
     *
     * @param group the group that should be checked
     */
    public void userInGroup(String group) {

        // configure search settings
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);

        try {

            // retrieve given group
            NamingEnumeration<?> namingEnum = this.context.search("cn=" + group + ",dc=nodomain,dc=com", "(objectclass=posixGroup)", searchControls);

            while (namingEnum.hasMore()) {
                SearchResult result = (SearchResult) namingEnum.next();
                Attributes attrs = result.getAttributes ();

                System.out.print("Authorization for group " + group + " ");

                // Check if user is in group
                if (attrs.get("memberUID") != null && attrs.get("memberUid").contains(this.username))
                    System.out.println("OK");
                else
                    System.out.println("NOT OK");

            }
        } catch (NamingException e) {
            System.out.println(e.getExplanation());
        }
    }

    /**
     * This method is used to disconnect from the ldap server
     */
    public void disconnect() {
        try {
            this.context.close();
        } catch (NamingException e) {
            System.out.println(e.getExplanation());
        }
    }

}
