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
            System.exit(-1);
        }

    }

    public String getDescription() {
        NamingEnumeration results = this.search("dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=" + GROUP + "))");
        return getAttributeValue(results, "description");
    }

    public void setDescription(String description) {
        updateAttribute("cn=" + GROUP + ",dc=nodomain,dc=com", "description", description);
    }

    private String getAttributeValue(NamingEnumeration namingEnum, String attributeName) {
        try {
            while (namingEnum.hasMore()) {
                SearchResult sr = (SearchResult) namingEnum.next();
                if (sr.getAttributes().get(attributeName) != null) {
                    return sr.getAttributes().get(attributeName).get().toString();
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateAttribute(String inDN, String inAttribute, String inValue) {

        ModificationItem[] mods = new ModificationItem[1];
        Attribute mod0 = new BasicAttribute(inAttribute, inValue);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
        try {
            this.context.modifyAttributes(inDN, mods);
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public NamingEnumeration search(String inBase, String inFilter) {
        // Search for objects using filter
        try {
            return this.context.search(inBase, inFilter, new SearchControls());
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
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
